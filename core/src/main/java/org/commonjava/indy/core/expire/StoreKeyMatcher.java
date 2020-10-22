/**
 * Copyright (C) 2011-2020 Red Hat, Inc. (https://github.com/Commonjava/indy)
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
package org.commonjava.indy.core.expire;

import org.commonjava.indy.model.core.StoreKey;
import org.commonjava.indy.subsys.infinispan.CacheHandle;
import org.commonjava.indy.subsys.infinispan.CacheKeyMatcher;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.apache.lucene.search.Query;

import java.util.HashSet;
import java.util.Set;

/**
 * A key matcher which is used to match the cache key with store key.
 *
 */
public class StoreKeyMatcher
        implements CacheKeyMatcher<ScheduleKey>
{
    private final String groupName;

    public StoreKeyMatcher( final StoreKey key, final String eventType )
    {
        this.groupName = ScheduleManager.groupName( key, eventType );
    }

    @Override
    public Set matches( CacheHandle<ScheduleKey, ?> cacheHandle )
    {
        SearchManager sm = Search.getSearchManager( cacheHandle.getCache() );
        Query lq = sm.buildQueryBuilderForClass( ScheduleKey.class ).get().keyword().onField( "groupName" ).matching( this.groupName ).createQuery();
        return new HashSet<>( sm.getQuery( lq ).list() ) ;
    }
}
