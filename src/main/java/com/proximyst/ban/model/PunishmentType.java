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

package com.proximyst.ban.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A form of punishment against a player.
 * <p>
 * All punishment types are permanently on the history of a player. Punishments that do not have an impact on the player
 * after it has been issued are therefore not liftable.
 */
public enum PunishmentType {
  /**
   * A warning on the player.
   */
  WARNING(0, false),

  /**
   * A mute on the player.
   * <p>
   * This means the player cannot communicate with other players.
   */
  MUTE(1),

  /**
   * A kick on the player.
   * <p>
   * This means the player was forcefully disconnected once.
   */
  KICK(2, false),

  /**
   * A ban on the player.
   * <p>
   * This means the player was forcefully removed from the server for a period of time.
   */
  BAN(3),

  /**
   * A note on the player.
   * <p>
   * This is not visible to the player in any way and is only used for the history.
   */
  NOTE(4, false),
  ;

  /**
   * All {@link PunishmentType}s by their IDs.
   */
  private static final Map<Byte, PunishmentType> PUNISHMENT_TYPES_BY_ID = new HashMap<>();

  static {
    for (PunishmentType type : values()) {
      PUNISHMENT_TYPES_BY_ID.put(type.getId(), type);
    }
  }

  /**
   * The ID of this {@link PunishmentType} in the database.
   */
  private final byte id;
  /**
   * Whether this {@link PunishmentType} can be lifted.
   */
  private final boolean canLift;

  PunishmentType(byte id, boolean canLift) {
    this.id = id;
    this.canLift = canLift;
  }

  PunishmentType(int id) {
    this(id, true);
  }

  PunishmentType(int id, boolean canLift) {
    this((byte) id, canLift);
  }

  /**
   * @param id The ID of the punishment type.
   * @return An {@link Optional} possibly containing a {@link PunishmentType}.
   */
  @NonNull
  public static Optional<PunishmentType> getById(byte id) {
    return Optional.ofNullable(PUNISHMENT_TYPES_BY_ID.get(id));
  }

  /**
   * @return The ID of this {@link PunishmentType} in the database.
   */
  public byte getId() {
    return id;
  }

  /**
   * @return Whether this {@link PunishmentType} can be lifted.
   */
  public boolean canBeLifted() {
    return canLift;
  }
}
