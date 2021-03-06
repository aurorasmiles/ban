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

package com.proximyst.ban.boilerplate.model;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A migration entry with the data version and path to its SQL file.
 */
public final class MigrationIndexEntry {
  @NonNegative
  private int version;

  @MonotonicNonNull
  private String path;

  public MigrationIndexEntry(int version, String path) {
    this.version = version;
    this.path = path;
  }

  public MigrationIndexEntry() {
  }

  /**
   * @return The version in the database this migration represents.
   */
  public int getVersion() {
    return version;
  }

  /**
   * @return The path for the migration SQL file.
   */
  @NonNull
  public String getPath() {
    return path;
  }
}
