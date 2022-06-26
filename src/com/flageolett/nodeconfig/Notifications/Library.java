package com.flageolett.nodeconfig.Notifications;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

public class Library {
    public static void send(Project project)
    {
        String spaces = StringUtils.repeat("&nbsp;", 32);
        String content = "<p>Enable node-config completions?</p><br /><a href='1'>Yes</a>" + spaces + "<a href='0'>No</a>";

        NotificationGroupManager.getInstance()
            .getNotificationGroup("NodeConfig")
            .createNotification(content, NotificationType.INFORMATION)
            .setListener(new Listener(project))
            .notify(project);
    }
}
