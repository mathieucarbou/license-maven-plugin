/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license;

/**
 * Simple wrapper class to transport login/password information.
 */
public class Credentials {
  private final String login;
  private final String password;

  /**
   * Instantiates a new credentials.
   *
   * @param login the login
   * @param password the password
   */
  public Credentials(String login, String password) {
    this.login = login;
    this.password = password;
  }

  /**
   * Gets the login.
   *
   * @return the login
   */
  public String getLogin() {
    return login;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }
}
