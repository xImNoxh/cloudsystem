package de.polocloud.api.util.system.java.version;

import org.jetbrains.annotations.NotNull;

public class JavaVersion implements Comparable<JavaVersion>{

    private final String version;

    public JavaVersion(String version) {
        this.version = version;
    }


    @Override
    public int compareTo(@NotNull JavaVersion o) {
        String[] currentVersion = this.getVersion().split("_")[0].split("\\.");
        String[] equalVersion = o.getVersion().split("_")[0].split("\\.");
        int length = Math.max(currentVersion.length, equalVersion.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < currentVersion.length ?
                Integer.parseInt(currentVersion[i]) : 0;
            int thatPart = i < equalVersion.length ?
                Integer.parseInt(equalVersion[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(this.getClass() != o.getClass())
            return false;
        return this.compareTo((JavaVersion) o) == 0;
    }

    public String getVersion() {
        return version;
    }
}
