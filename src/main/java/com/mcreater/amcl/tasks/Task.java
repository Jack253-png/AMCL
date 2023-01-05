package com.mcreater.amcl.tasks;

import java.io.IOException;

public interface Task {
    String DEFAULT_POOL = "<default>";
    Integer execute() throws IOException;
}
