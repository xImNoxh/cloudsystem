package de.polocloud.master.group;

import de.polocloud.database.repository.RepositoryPool;
import de.polocloud.master.group.repository.GroupRepository;

public class GroupService {

    private static GroupService instance;

    public GroupService() {
        instance = this;
    }

    public static GroupService getInstance() {
        return instance;
    }

    public GroupRepository getRepository(){
        return RepositoryPool.getRepository(GroupRepository.class);
    }

}
