//
// ban - A punishment suite for Velocity.
// Copyright (C) 2020 Mariell Hoversholm
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published
// by the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package com.proximyst.ban.manager;

import com.google.inject.Singleton;
import com.proximyst.ban.BanPermissions;
import com.proximyst.ban.BanPlugin;
import com.proximyst.ban.config.MessagesConfig;
import com.proximyst.ban.model.BanUser;
import com.proximyst.ban.model.Punishment;
import com.proximyst.ban.model.PunishmentType;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@Singleton
public final class MessageManager {
  @NonNull
  private final BanPlugin main;

  @NonNull
  private final MessagesConfig cfg;

  public MessageManager(
      @NonNull BanPlugin main,
      @NonNull MessagesConfig cfg
  ) {
    this.main = main;
    this.cfg = cfg;
  }

  @NonNull
  public Optional<@NonNull String> getNotificationPermissionOf(@NonNull PunishmentType type) {
    switch (type) {
      case BAN:
        return Optional.of(BanPermissions.NOTIFY_BAN);
      case KICK:
        return Optional.of(BanPermissions.NOTIFY_KICK);
      case MUTE:
        return Optional.of(BanPermissions.NOTIFY_MUTE);
      case WARNING:
        return Optional.of(BanPermissions.NOTIFY_WARN);

      case NOTE:
        // Fall through
      default:
        return Optional.empty();
    }
  }

  @NonNull
  public CompletableFuture<@NonNull Optional<@NonNull Component>> notificationMessage(@NonNull Punishment punishment) {
    String message = null;
    switch (punishment.getPunishmentType()) {
      case BAN:
        if (punishment.isLifted()) {
          message = cfg.broadcasts.unban;
        } else {
          message = punishment.getReason().isPresent() ? cfg.broadcasts.banReason : cfg.broadcasts.banReasonless;
        }
        break;
      case KICK:
        // No #isLifted; a kick cannot be lifted.
        message = punishment.getReason().isPresent() ? cfg.broadcasts.kickReason : cfg.broadcasts.kickReasonless;
        break;
      case MUTE:
        if (punishment.isLifted()) {
          message = cfg.broadcasts.unmute;
        } else {
          message = punishment.getReason().isPresent() ? cfg.broadcasts.muteReason : cfg.broadcasts.muteReasonless;
        }
        break;
      case WARNING:
        // It shouldn't actually get here, but let's make sure it doesn't go farther at the very least.
        if (!punishment.isLifted()) {
          message = punishment.getReason().isPresent() ? cfg.broadcasts.warnReason : cfg.broadcasts.warnReasonless;
        }
        break;

      case NOTE:
        // Fall through
      default:
        return CompletableFuture.completedFuture(Optional.empty());
    }

    if (message == null) {
      return CompletableFuture.completedFuture(Optional.empty());
    }
    if (message.trim().isEmpty()) {
      return CompletableFuture.completedFuture(Optional.of(TextComponent.empty()));
    }

    return formatMessageWith(message, punishment)
        .thenApply(Optional::of);
  }

  @NonNull
  public Component errorNoBan(@NonNull BanUser user) {
    return MiniMessage.get().parse(
        cfg.errors.noBan,

        "targetName", user.getUsername(),
        "targetUuid", user.getUuid().toString()
    );
  }

  @NonNull
  public CompletableFuture<@NonNull Component> formatMessageWith(
      @NonNull String message,
      @NonNull Punishment punishment
  ) {
    return main.getUserManager().getUser(punishment.getTarget())
        .thenApply(opt ->
            opt.orElseThrow(() -> new IllegalArgumentException("Target of punishment cannot be unknown."))
        )
        .thenCombine(
            main.getUserManager().getUser(punishment.getPunisher()),
            (target, $punisher) -> {
              BanUser punisher = $punisher.orElseThrow(
                  () -> new IllegalArgumentException("Punisher of punishment cannot be unknown.")
              );
              return MiniMessage.get()
                  .parse(
                      message,

                      "targetName", target.getUsername(),
                      "targetUuid", target.getUuid().toString(),

                      "punisherName", punisher.getUsername(),
                      "punisherUuid", punisher.getUuid().toString(),

                      "punishmentId", punishment.getId().orElse(-1L).toString(),
                      "punishmentDate", SimpleDateFormat.getDateInstance().format(punishment.getDate()),
                      "reason", punishment.getReason()
                          .map(MiniMessage.get()::escapeTokens)
                          .orElse("No reason specified"),

                      "duration", punishment.isPermanent()
                          ? cfg.formatting.permanently
                          : cfg.formatting.durationFormat
                              .replace("<duration>",
                                  DurationFormatUtils.formatDurationWords(
                                      punishment.getExpiration() - System.currentTimeMillis(),
                                      false,
                                      false
                                  ))
                  );
            }
        );
  }
}
