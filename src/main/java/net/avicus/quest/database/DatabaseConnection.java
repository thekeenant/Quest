package net.avicus.quest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import lombok.Getter;

public class DatabaseConnection {

  @Getter
  private final Database database;
  private Optional<Connection> connection;

  public DatabaseConnection(Database database) {
    this.database = database;
    this.connection = Optional.empty();
  }

  public void open() throws DatabaseException {
    try {
      String url = this.database.getConfig().getUrl();
      String username = this.database.getConfig().getUsername();
      String password = this.database.getConfig().getPassword();
      this.connection = Optional.of(DriverManager.getConnection(url, username, password));
    } catch (SQLException e) {
      throw new DatabaseException("Connection failed.", e);
    }
  }

  public void close() {
    if (this.connection.isPresent()) {
      try {
        this.connection.get().close();
      } catch (SQLException e) {
        // ignored
      }
    }
  }

  public boolean getStatus() {
    return this.connection.isPresent();
  }

  public Connection getConnection() throws DatabaseException {
    if (this.connection.isPresent()) {
      return this.connection.get();
    }
    throw new DatabaseException("Connection has not been made.");
  }
}
