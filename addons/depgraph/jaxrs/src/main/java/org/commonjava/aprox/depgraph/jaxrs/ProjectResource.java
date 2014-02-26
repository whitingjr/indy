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
package org.commonjava.aprox.depgraph.jaxrs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.commonjava.aprox.bind.jaxrs.util.AproxExceptionUtils;
import org.commonjava.aprox.depgraph.rest.ProjectController;
import org.commonjava.aprox.rest.AproxWorkflowException;
import org.commonjava.maven.atlas.ident.DependencyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path( "/depgraph/project" )
@Produces( MediaType.APPLICATION_JSON )
@ApplicationScoped
public class ProjectResource
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private ProjectController controller;

    @Path( "/{g}/{a}/{v}/errors" )
    @GET
    public Response errors( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId, @PathParam( "v" ) final String version )
    {
        String json;
        try
        {
            json = controller.errors( groupId, artifactId, version );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/list" )
    @GET
    public Response list( @QueryParam( "g" ) final String groupIdPattern, @QueryParam( "a" ) final String artifactIdPattern )
    {
        String json;
        try
        {
            json = controller.list( groupIdPattern, artifactIdPattern );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/parent" )
    @GET
    public Response parentOf( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                              @PathParam( "v" ) final String version )
    {
        String json;
        try
        {
            json = controller.parentOf( groupId, artifactId, version );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/dependencies" )
    @GET
    public Response dependenciesOf( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                                    @PathParam( "v" ) final String version, @QueryParam( "scopes" ) final String scopesStr )
    {
        String json;
        try
        {
            json = controller.dependenciesOf( groupId, artifactId, version, DependencyScope.parseScopes( scopesStr ) );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/plugins" )
    @GET
    public Response pluginsOf( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                               @PathParam( "v" ) final String version )
    {
        String json;
        try
        {
            json = controller.pluginsOf( groupId, artifactId, version );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/extensions" )
    @GET
    public Response extensionsOf( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                                  @PathParam( "v" ) final String version )
    {
        String json;
        try
        {
            json = controller.extensionsOf( groupId, artifactId, version );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/relationships" )
    @GET
    public Response relationshipsSpecifiedBy( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                                              @PathParam( "v" ) final String version, @Context final HttpServletRequest request )
    {
        String json;
        try
        {
            json = controller.relationshipsSpecifiedBy( groupId, artifactId, version, request.getParameterMap() );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

    @Path( "/{g}/{a}/{v}/users" )
    @GET
    public Response relationshipsTargeting( @PathParam( "g" ) final String groupId, @PathParam( "a" ) final String artifactId,
                                            @PathParam( "v" ) final String version, @Context final HttpServletRequest request )
    {
        String json;
        try
        {
            json = controller.relationshipsTargeting( groupId, artifactId, version, request.getParameterMap() );
        }
        catch ( final AproxWorkflowException e )
        {
            logger.error( e.getMessage(), e );
            return AproxExceptionUtils.formatResponse( e );
        }

        return json == null ? Response.noContent()
                                      .build() : Response.ok( json )
                                                         .build();
    }

}
