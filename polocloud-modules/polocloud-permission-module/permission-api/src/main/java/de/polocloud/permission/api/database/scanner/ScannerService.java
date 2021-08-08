package de.polocloud.permission.api.database.scanner;

import de.polocloud.permission.api.database.adapter.DatabaseService;

public class ScannerService {

    public ScannerService(ClassLoader classLoader, DatabaseService databaseService) {
        new EntityProcessor(classLoader, databaseService);
        new RepositoryProcessor(classLoader, databaseService);
    }


}
