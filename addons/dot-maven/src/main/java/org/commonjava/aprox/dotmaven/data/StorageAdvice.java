package org.commonjava.aprox.dotmaven.data;

import org.commonjava.aprox.model.ArtifactStore;
import org.commonjava.aprox.model.DeployPoint;

public class StorageAdvice
{
    private final boolean deployable;

    private final boolean releasesAllowed;

    private final boolean snapshotsAllowed;

    private final ArtifactStore store;

    private final DeployPoint deployableStore;

    public StorageAdvice( final ArtifactStore store, final DeployPoint deployableStore, final boolean deployable,
                          final boolean releasesAllowed, final boolean snapshotsAllowed )
    {
        this.store = store;
        this.deployableStore = deployableStore;
        this.deployable = deployable;
        this.releasesAllowed = releasesAllowed;
        this.snapshotsAllowed = snapshotsAllowed;
    }

    public DeployPoint getDeployableStore()
    {
        return deployableStore;
    }

    public ArtifactStore getStore()
    {
        return store;
    }

    public boolean isDeployable()
    {
        return deployable;
    }

    public boolean isReleasesAllowed()
    {
        return releasesAllowed;
    }

    public boolean isSnapshotsAllowed()
    {
        return snapshotsAllowed;
    }

}