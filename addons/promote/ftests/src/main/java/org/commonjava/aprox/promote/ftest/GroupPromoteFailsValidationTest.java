/**
 * Copyright (C) 2011 Red Hat, Inc. (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.aprox.promote.ftest;

import org.commonjava.aprox.model.core.ArtifactStore;
import org.commonjava.aprox.model.core.Group;
import org.commonjava.aprox.model.core.StoreType;
import org.commonjava.aprox.promote.client.AproxPromoteClientModule;
import org.commonjava.aprox.promote.model.GroupPromoteRequest;
import org.commonjava.aprox.promote.model.GroupPromoteResult;
import org.commonjava.aprox.promote.model.ValidationResult;
import org.commonjava.aprox.test.fixture.core.CoreServerFixture;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class GroupPromoteFailsValidationTest
    extends AbstractPromotionManagerTest
{

    @Test
    public void promoteToGroup_PushTwoArtifactsToHostedRepo_VerifyAvailableViaGroupMembership()
        throws Exception
    {
        final GroupPromoteResult result = client.module( AproxPromoteClientModule.class )
                                           .promoteToGroup(
                                                   new GroupPromoteRequest( source.getKey(), target.getName() ) );

        assertFalse( result.succeeded() );

        assertThat( result.getRequest().getSource(), equalTo( source.getKey() ) );
        assertThat( result.getRequest()
                          .getTargetGroup(), equalTo( target.getName() ) );

        assertThat( result.getError(), nullValue() );

        ValidationResult validations = result.getValidations();
        assertThat( validations, notNullValue() );

        Map<String, String> errors = validations.getValidatorErrors();
        assertThat( errors, notNullValue() );

        String error = errors.get( "fail-all.groovy" );
        assertThat( error, notNullValue() );

        assertThat( client.content().exists( target.getKey().getType(), target.getName(), first ), equalTo( false ) );
        assertThat( client.content().exists( target.getKey().getType(), target.getName(), second ), equalTo( false ) );

        Group g = client.stores().load( StoreType.group, target.getName(), Group.class );
        assertThat( g.getConstituents().contains( source.getKey() ), equalTo( false ) );
    }

    @Override
    protected ArtifactStore createTarget( String changelog )
            throws Exception
    {
        Group group = new Group( "test" );
        return client.stores().create( group, changelog, Group.class );
    }

    @Override
    protected void initTestData( CoreServerFixture fixture )
            throws IOException
    {
        writeDataFile( "promote/rules/fail-all.groovy", readTestResource( getClass().getSimpleName() + "/fail-all.groovy" ) );
        writeDataFile( "promote/rule-sets/fail-all.json", readTestResource( getClass().getSimpleName() + "/fail-all.json" ) );
    }
}
