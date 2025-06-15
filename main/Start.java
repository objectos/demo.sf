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
  // A note sink works as a logger for this particular application
  // Objectos Way provides implementations in the App namespace.
  final Note.Sink noteSink;
  noteSink = App.NoteSink.OfConsole.create();

  // Convenience for registering tasks to be executed
  // in the JVM shutdown sequence.
  // We'll typically register AutoCloseable instances
  final App.ShutdownHook shutdownHook;
  shutdownHook = App.ShutdownHook.create(opts -> {
    opts.noteSink(noteSink);
  });

  // All HTTP requests will be handled by this object.
  // Therefore, handlers themselves MUST BE stateless.
  // State, e.g. web session, must be stored elsewhere (Http.SessionStore).
  final Http.Handler handler;
  handler = Http.Handler.create(this::routes);

  // The Objectos Way HTTP/1.1 server instance.
  // Binds to the loopback address,
  // and uses one virtual thread per connection.
  final Http.Server server;
  server = Http.Server.create(opts -> {
    opts.handler(handler);

    opts.noteSink(noteSink);

    opts.port(8080);
  });

  // Closes (stops) the HTTP server during the JVM shutdown sequence.
  shutdownHook.register(server);

  // Starts the HTTP server
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

  // if the request does not match any of the previous routes,
  // it will be handled here
  routing.handler(http -> {
    http.notFound(Media.Bytes.textPlain("Not Found"));
  });
}

/**
 * Renders the home page of our application.
 */
private static final class Home extends Html.Template {
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
  final Home view;
  view = new Home();

  http.ok(view);
}

/**
 * Renders the Objectos HTML demo of our application.
 */
private static final class ObjectosHtml extends Html.Template {
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
  // The template variables 'variable'
  final String name;
  name = http.queryParam("name");

  if (name == null) {
    final Media message;
    message = Media.Bytes.textPlain("Please specify a name query parameter");

    http.badRequest(message);

    return;
  }

  // The conditional rendering variable
  final String showParam;
  showParam = http.queryParam("show");

  final boolean show;
  show = "on".equals(showParam);

  // The loops/iteration variable
  int count;
  count = http.queryParamAsInt("count", 1);

  if (count < 0) {
    count = 1;
  }

  final ObjectosHtml view;
  view = new ObjectosHtml(name, show, count);

  http.ok(view);
}
