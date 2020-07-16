package com.proximyst.ban.config;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class Configuration {
  @Setting(comment = "The SQL server settings.")
  private SqlConfig sql = new SqlConfig();

  @Setting(comment = "Whether to use Ashcon instead of the official Mojang API.")
  private boolean useAshcon = true;

  @NonNull
  public SqlConfig getSql() {
    return sql;
  }

  public boolean useAshcon() {
    return useAshcon;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Configuration that = (Configuration) o;
    return getSql().equals(that.getSql())
        && useAshcon() == that.useAshcon();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSql(), useAshcon());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("sql", getSql())
        .add("useAshcon", useAshcon())
        .toString();
  }
}
