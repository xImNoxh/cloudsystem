package de.polocloud.database.scanner;

import de.polocloud.database.DatabaseService;

public class ScannerService {

    public ScannerService(ClassLoader classLoader, DatabaseService databaseService) {
        new EntityProcessor(classLoader, databaseService);
        new RepositoryProcessor(classLoader, databaseService);
    }

}
