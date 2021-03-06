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

import com.google.common.collect.ImmutableList;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jdbi.v3.core.result.RowView;

public final class UsernameHistory {
  @NonNull
  private final UUID uuid;

  @NonNull
  private final ImmutableList<@NonNull Entry> entries;

  public UsernameHistory(@NonNull UUID uuid, @NonNull Iterable<? extends UsernameHistory.Entry> entries) {
    this.uuid = uuid;
    this.entries = ImmutableList.sortedCopyOf(
        Comparator.comparingLong(
            entry -> entry
                .getChangedAt()
                .map(Date::getTime)
                .orElse(Long.MIN_VALUE) // Original is first in the list.
        ),
        entries
    );
  }

  @NonNull
  public ImmutableList<@NonNull Entry> getEntries() {
    return entries;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", UsernameHistory.class.getSimpleName() + "[", "]")
        .add("uuid=" + uuid)
        .add("entries=" + entries)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UsernameHistory that = (UsernameHistory) o;
    return uuid.equals(that.uuid) &&
        entries.equals(that.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, entries);
  }

  public static class Entry {
    @NonNull
    private final String username;

    @Nullable
    private final Date changedAt;

    public Entry(@NonNull String username, @Nullable Date changedAt) {
      this.username = username;
      this.changedAt = changedAt;
    }

    @NonNull
    public static Entry fromRow(@NonNull RowView view) {
      return new Entry(
          view.getColumn("username", String.class),
          Optional.ofNullable(view.getColumn("timestamp", Timestamp.class))
              .map(stamp -> {
                return Date.from(stamp.toInstant());
              })
              .orElse(null)
      );
    }

    @NonNull
    public String getUsername() {
      return username;
    }

    @NonNull
    public Optional<Date> getChangedAt() {
      return Optional.ofNullable(changedAt);
    }

    public boolean isOriginal() {
      return changedAt == null;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Entry entry = (Entry) o;
      return getUsername().equals(entry.getUsername()) &&
          Objects.equals(getChangedAt(), entry.getChangedAt());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getUsername(), getChangedAt());
    }

    @Override
    public String toString() {
      return "Entry{" +
          "username='" + username + '\'' +
          ", changedAt=" + changedAt +
          '}';
    }
  }
}
