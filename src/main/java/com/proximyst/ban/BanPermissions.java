package com.proximyst.ban;

public final class BanPermissions {
  private static final String BASE = "ban.";
  private static final String BASE_COMMANDS = BASE + "commands.";
  private static final String BASE_NOTIFY = BASE + "notify.";

  public static final String COMMAND_BAN = BASE_COMMANDS + "ban";
  public static final String COMMAND_UNBAN = BASE_COMMANDS + "unban";

  public static final String NOTIFY_BAN = BASE_NOTIFY + "ban";
  public static final String NOTIFY_KICK = BASE_NOTIFY + "kick";
  public static final String NOTIFY_MUTE = BASE_NOTIFY + "mute";
  public static final String NOTIFY_WARN = BASE_NOTIFY + "warn";

  private BanPermissions() throws IllegalAccessException {
    throw new IllegalAccessException(getClass().getSimpleName() + " cannot be instantiated.");
  }
}
