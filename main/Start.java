/*
 * Copyright (C) 2025 Objectos Software LTDA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import module objectos.way;

/**
 * Bootstraps and starts the application.
 */
void main() throws java.io.IOException {
  var noteSink = App.NoteSink.OfConsole.create();

  var shutdownHook = App.ShutdownHook.create(opts -> {
    opts.noteSink(noteSink);
  });

  var handler = Http.Handler.create(this::routes);

  var server = Http.Server.create(opts -> {
    opts.handler(handler);

    opts.noteSink(noteSink);

    opts.port(8080);
  });

  shutdownHook.register(server);

  server.start();
}

/**
 * Registers the routes of the application
 */
private void routes(Http.Routing routing) {
  routing.path("/", path -> {
    path.allow(Http.Method.GET, this::home);
  });

  routing.path("/objectos/html", path -> {
    path.allow(Http.Method.GET, this::objectosHtml);
  });

  routing.handler(http -> {
    http.notFound(Media.Bytes.textPlain("Not Found"));
  });
}

/**
 * Renders the Objectos HTML demo of our application.
 */
static final class ObjectosHtml extends Html.Template {
  private final String name;

  private final boolean show;

  private final int count;

  ObjectosHtml(String name, boolean show, int count) {
    this.name = name;

    this.show = show;

    this.count = count;
  }

  @Override
  protected final void render() {
    doctype();
    html(
        head(
            title("Objectos Way In A Single File #003")
        ),

        body(
            h1("This page showcases the Objectos HTML features"),

            p("It's the Objectos Way!"),

            h2("Template variables"),

            p(text("Hello, "), strong(name)),

            h2("Conditional rendering"),

            show ? p("I'm shown!!!") : noop(),

            h2("Loops / iteration"),

            ul(
                f(this::renderItems)
            )
        )
    );
  }

  private void renderItems() {
    for (int i = 0; i < count; i++) {
      li("Objectos HTML Is Cool!");
    }
  }
}

/**
 * The Objectos HTML "controller".
 */
private void objectosHtml(Http.Exchange http) {
  String name = http.queryParam("name");

  if (name == null) {
    Media message = Media.Bytes.textPlain("Please specify a name query parameter");

    http.badRequest(message);

    return;
  }

  String showParam = http.queryParam("show");

  boolean show = "on".equals(showParam);

  int count = http.queryParamAsInt("count", 1);

  if (count < 0) {
    count = 1;
  }

  ObjectosHtml view = new ObjectosHtml(name, show, count);

  http.ok(view);
}

/**
 * Renders the home page of our application.
 */
static final class Home extends Html.Template {
  @Override
  protected final void render() {
    doctype();
    html(
        head(
            title("Objectos Way In A Single File #002")
        ),

        body(
            h1("This website is built entirely using Java"),

            p("It's the Objectos Way!")
        )
    );
  }
}

/**
 * The home page "controller".
 */
private void home(Http.Exchange http) {
  Home view = new Home();

  http.ok(view);
}
