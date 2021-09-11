Simple MainServer for PoloCloud

The Server refreshes the config every 30 Seconds. In the config you provide the versions of the "api" and the "
bootstrap" and also the file name of the "api" and the "bootstrap"

To create a new PoloCloud Client instance you need the IP and the port of the server, then you use:

```java
  PoloCloudClient client = new PoloCloudClient(ip,port);
```

To report an exception, you need the "throwable", the type ("master, wrapper, launcher") and the version of the cloud,
then you do:

```java
client.getExceptionReportService().reportException(trowable,"master","0.2");
```

When you want to update a file you need to have created an instance of the PoloCloudClient, then you do:

```java
PoloCloudUpdater updater = new PoloCloudUpdater(devmode, currentVersion, type("boostrap", "api"), targetFile(new File("boostrap.jar"));
updater.autoDownload();
```
