/**
 * Copyright (C) 2013 Red Hat, Inc.
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
package org.commonjava.indy.content.browse.model;

import org.commonjava.indy.model.core.StoreKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContentBrowseResult
{
    private StoreKey storeKey;

    private String parentUrl;

    private String parentPath;

    private String path;

    private String storeBrowseUrl;

    private String storeContentUrl;

    private String baseBrowseUrl;

    private String baseContentUrl;

    private List<String> sources;

    private Map<String, Set<String>> listingUrls;

    public StoreKey getStoreKey()
    {
        return storeKey;
    }

    public void setStoreKey( StoreKey storeKey )
    {
        this.storeKey = storeKey;
    }

    public String getParentUrl()
    {
        return parentUrl;
    }

    public void setParentUrl( String parentUrl )
    {
        this.parentUrl = parentUrl;
    }

    public String getParentPath()
    {
        return parentPath;
    }

    public void setParentPath( String parentPath )
    {
        this.parentPath = parentPath;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getStoreBrowseUrl()
    {
        return storeBrowseUrl;
    }

    public void setStoreBrowseUrl( String storeBrowseUrl )
    {
        this.storeBrowseUrl = storeBrowseUrl;
    }

    public String getStoreContentUrl()
    {
        return storeContentUrl;
    }

    public void setStoreContentUrl( String storeContentUrl )
    {
        this.storeContentUrl = storeContentUrl;
    }

    public String getBaseBrowseUrl()
    {
        return baseBrowseUrl;
    }

    public void setBaseBrowseUrl( String baseBrowseUrl )
    {
        this.baseBrowseUrl = baseBrowseUrl;
    }

    public String getBaseContentUrl()
    {
        return baseContentUrl;
    }

    public void setBaseContentUrl( String baseContentUrl )
    {
        this.baseContentUrl = baseContentUrl;
    }

    public List<String> getSources()
    {
        return sources;
    }

    public void setSources( List<String> sources )
    {
        this.sources = sources;
    }

    public Map<String, Set<String>> getListingUrls()
    {
        return listingUrls;
    }

    public void setListingUrls( Map<String, Set<String>> listingUrls )
    {
        this.listingUrls = listingUrls;
    }
}
