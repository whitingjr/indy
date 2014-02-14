/*******************************************************************************
 * Copyright (C) 2014 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.aprox.autoprox.data;

import static org.commonjava.aprox.util.UrlUtils.buildUrl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpHead;
import org.commonjava.aprox.autoprox.conf.AutoProxConfiguration;
import org.commonjava.aprox.autoprox.conf.AutoProxModel;
import org.commonjava.aprox.data.ProxyDataException;
import org.commonjava.aprox.data.StoreDataManager;
import org.commonjava.aprox.model.ArtifactStore;
import org.commonjava.aprox.model.Group;
import org.commonjava.aprox.model.HostedRepository;
import org.commonjava.aprox.model.RemoteRepository;
import org.commonjava.aprox.model.StoreKey;
import org.commonjava.aprox.model.StoreType;
import org.commonjava.aprox.subsys.http.AproxHttpProvider;
import org.commonjava.util.logging.Logger;

@Decorator
public abstract class AutoProxDataManagerDecorator
    implements StoreDataManager
{

    private static final String REPO_NAME_URL_PATTERN = Pattern.quote( "${name}" );

    private static final String REPO_CONSTITUENT_PLACEHOLDER = "${repository}";

    private static final String DEPLOY_CONSTITUENT_PLACEHOLDER = "${deploy}";

    private final Logger logger = new Logger( getClass() );

    @Delegate
    @Any
    @Inject
    private StoreDataManager dataManager;

    @Inject
    private AutoProxConfiguration config;

    @Inject
    private AutoProxModel autoproxModel;

    @Inject
    private AproxHttpProvider http;

    @Override
    public Group getGroup( final String name )
        throws ProxyDataException
    {
        //        logger.info( "DECORATED (getGroup: %s)", name );
        Group g = dataManager.getGroup( name );

        if ( !config.isEnabled() )
        {
            //            logger.info( "AutoProx decorator disabled; returning: %s", g );
            return g;
        }

        //        logger.info( "AutoProx decorator active" );
        if ( g == null )
        {
            //            logger.info( "AutoProx: creating repository for: %s", name );
            final RemoteRepository proxy = getRemoteRepository( name );
            if ( proxy != null )
            {
                HostedRepository dp = null;
                if ( config.isDeployEnabled() )
                {
                    dp = dataManager.getHostedRepository( name );

                    if ( dp == null )
                    {
                        dp = new HostedRepository( name );

                        final HostedRepository deployTemplate = autoproxModel.getHostedRepository();

                        if ( deployTemplate != null )
                        {
                            dp.setAllowReleases( deployTemplate.isAllowReleases() );
                            dp.setAllowSnapshots( deployTemplate.isAllowSnapshots() );
                            dp.setSnapshotTimeoutSeconds( deployTemplate.getSnapshotTimeoutSeconds() );
                        }

                        dataManager.storeHostedRepository( dp );
                    }
                }

                g = new Group( name );

                boolean rFound = false;
                boolean dFound = false;
                final Group groupTemplate = autoproxModel.getGroup();

                if ( groupTemplate != null && groupTemplate.getConstituents() != null )
                {
                    for ( final StoreKey storeKey : groupTemplate.getConstituents() )
                    {
                        if ( storeKey.getType() == StoreType.remote && REPO_CONSTITUENT_PLACEHOLDER.equalsIgnoreCase( storeKey.getName() ) )
                        {
                            g.addConstituent( proxy );
                            rFound = true;
                        }
                        else if ( dp != null && storeKey.getType() == StoreType.hosted
                            && DEPLOY_CONSTITUENT_PLACEHOLDER.equalsIgnoreCase( storeKey.getName() ) )
                        {
                            g.addConstituent( dp );
                            dFound = true;
                        }
                        else
                        {
                            g.addConstituent( storeKey );
                        }
                    }
                }

                final List<StoreKey> constituents = g.getConstituents();
                if ( !rFound )
                {
                    constituents.add( 0, proxy.getKey() );
                }

                if ( dp != null && !dFound )
                {
                    constituents.add( 0, dp.getKey() );
                }

                dataManager.storeGroup( g );
            }
        }

        return g;
    }

    private synchronized boolean checkUrlValidity( final RemoteRepository repo, final String proxyUrl, final String validationPath )
    {
        String url = null;
        try
        {
            url = buildUrl( proxyUrl, validationPath );
        }
        catch ( final MalformedURLException e )
        {
            logger.error( "Failed to construct repository-validation URL from base: %s and path: %s. Reason: %s", e, proxyUrl, validationPath,
                          e.getMessage() );
            return false;
        }

        //        logger.info( "\n\n\n\n\n[AutoProx] Checking URL: %s from:", new Throwable(), url );
        final HttpHead head = new HttpHead( url );

        http.bindRepositoryCredentialsTo( repo, head );

        boolean result = false;
        try
        {
            final HttpResponse response = http.getClient()
                                              .execute( head );
            final StatusLine statusLine = response.getStatusLine();
            final int status = statusLine.getStatusCode();
            //            logger.info( "[AutoProx] HTTP Status: %s", statusLine );
            result = status == HttpStatus.SC_OK;
        }
        catch ( final ClientProtocolException e )
        {
            logger.warn( "[AutoProx] Cannot connect to target repository: '%s'.", url );
        }
        catch ( final IOException e )
        {
            logger.warn( "[AutoProx] Cannot connect to target repository: '%s'.", url );
        }
        finally
        {
            http.clearRepositoryCredentials();
            http.closeConnection();
        }

        return result;
    }

    @Override
    public RemoteRepository getRemoteRepository( final String name )
        throws ProxyDataException
    {
        //        logger.info( "DECORATED (getRepository: %s)", name );
        RemoteRepository repo = dataManager.getRemoteRepository( name );
        if ( !config.isEnabled() )
        {
            //            logger.info( "AutoProx decorator disabled; returning: %s", repo );
            return repo;
        }

        //        logger.info( "AutoProx decorator active" );
        if ( repo == null )
        {
            //            logger.info( "AutoProx: creating repository for: %s", name );

            final RemoteRepository repoTemplate = autoproxModel.getRemoteRepository();
            final String validationPath = autoproxModel.getRepoValidationPath();

            final String url = resolveRepoUrl( repoTemplate.getUrl(), name );

            if ( repo == null )
            {
                repo = new RemoteRepository( name, url );

                repo.setCacheTimeoutSeconds( repoTemplate.getCacheTimeoutSeconds() );
                repo.setHost( repoTemplate.getHost() );
                repo.setKeyCertPem( repoTemplate.getKeyCertPem() );
                repo.setKeyPassword( repoTemplate.getKeyPassword() );
                repo.setPassthrough( repoTemplate.isPassthrough() );
                repo.setPassword( repoTemplate.getPassword() );
                repo.setPort( repoTemplate.getPort() );
                repo.setProxyHost( repoTemplate.getProxyHost() );
                repo.setProxyPassword( repoTemplate.getProxyPassword() );
                repo.setProxyPort( repoTemplate.getProxyPort() );
                repo.setProxyUser( repoTemplate.getProxyUser() );
                repo.setServerCertPem( repoTemplate.getServerCertPem() );
                repo.setTimeoutSeconds( repoTemplate.getTimeoutSeconds() );
                repo.setUser( repoTemplate.getUser() );

                if ( !checkUrlValidity( repo, url, validationPath ) )
                {
                    logger.warn( "Invalid repository URL: %s", url );
                    return null;
                }

                dataManager.storeRemoteRepository( repo );
            }
        }

        return repo;
    }

    private String resolveRepoUrl( final String src, final String name )
    {
        return src.replaceAll( REPO_NAME_URL_PATTERN, name );
    }

    @Override
    public ArtifactStore getArtifactStore( final StoreKey key )
        throws ProxyDataException
    {
        //        logger.info( "DECORATED (getArtifactStore: %s)", key );
        final StoreType type = key.getType();
        switch ( type )
        {
            case group:
            {
                return getGroup( key.getName() );
            }
            case remote:
            {
                return getRemoteRepository( key.getName() );
            }
            default:
            {
                return dataManager.getArtifactStore( key );
            }
        }
    }
}
