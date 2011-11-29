/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.aprox.sec.fixture;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.commonjava.couch.test.fixture.TestData;

@Singleton
public class AProxSecTestPropertiesProvider
{

    public static final String REPO_ROOT_DIR = "repo.root.dir";

    public static final String APROX_DATABASE_URL = "aprox.db.url";

    public static final String USER_DATABASE_URL = "user.db.url";

    @Produces
    @TestData
    public Properties getTestProperties()
    {
        final Properties props = new Properties();

        props.put( APROX_DATABASE_URL, "http://localhost:5984/test-aprox" );
        props.put( USER_DATABASE_URL, "http://localhost:5984/test-user" );
        props.put( REPO_ROOT_DIR, System.getProperty( REPO_ROOT_DIR, "target/repo-downloads" ) );

        return props;
    }

}
