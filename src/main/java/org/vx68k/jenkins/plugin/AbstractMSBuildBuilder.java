/*
 * AbstractMSBuildBuilder
 * Copyright (C) 2014 Kaz Nishimura
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.vx68k.jenkins.plugin;

import java.io.IOException;
import java.util.Map;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;

/**
 * Builds a MSBuild project.
 *
 * @author Kaz Nishimura
 * @since 2.0
 */
public abstract class AbstractMSBuildBuilder extends Builder {

    private static final String MSBUILD_COMMAND_NAME = "MSBuild.exe";

    private final String projectFile;
    private final String switches;

    public AbstractMSBuildBuilder(String projectFile, String switches) {
        this.projectFile = projectFile;
        this.switches = switches;
    }

    public String getProjectFile() {
        return projectFile;
    }

    public String getSwitches() {
        return switches;
    }

    protected boolean build(AbstractBuild<?, ?> build, Launcher launcher,
            FilePath framworkHome, Map<String, String> env,
            TaskListener listener) throws InterruptedException, IOException {
        FilePath msbuild = new FilePath(framworkHome, MSBUILD_COMMAND_NAME);

        Launcher.ProcStarter msbuildStarter = launcher.launch();
        msbuildStarter.stdout(listener.getLogger());
        msbuildStarter.stderr(listener.getLogger());
        msbuildStarter.envs(env);
        msbuildStarter.pwd(build.getWorkspace());

        ArgumentListBuilder args = new ArgumentListBuilder(
                msbuild.getRemote());
        args.addTokenized(getSwitches());
        if (!getProjectFile().isEmpty()) {
            args.add(getProjectFile());
        }
        msbuildStarter.cmds(args.toList());

        Proc msbuildProc = msbuildStarter.start();
        // Any error messages must already be printed.
        return msbuildProc.join() == 0;
    }
}