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
  handler = Http.Handler.of(this::routes);

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

  routing.path("/objectos/script", path -> {
    path.allow(Http.Method.GET, this::objectosScript);
  });

  routing.path("/script.js", path -> {
    path.allow(Http.Method.GET, http -> http.ok(Script.Library.of()));
  });

  routing.path("/styles.css", path -> {
    path.allow(Http.Method.GET, this::styles);
  });

  // if the request does not match any of the previous routes,
  // it will be handled here
  routing.handler(http -> {
    http.notFound(Media.Bytes.textPlain("Not Found"));
  });
}

/**
 * The base template of our application.
 */
private static abstract class Page extends Html.Template {
  @Override
  protected final void render() {
    doctype();
    html(
        css("""
        background-color:bg
        color:fg
        """),

        head(
            link(rel("stylesheet"), type("text/css"), href("/styles.css")),
            script(src("/script.js")),
            title(pageTitle())
        ),

        body(
            css("""
            display:flex
            align-items:center
            justify-content:center
            min-height:100dvh
            """),

            f(this::renderBody)
        )
    );
  }

  abstract String pageTitle();

  abstract void renderBody();
}

/**
 * Renders the home page of our application.
 */
private static final class Home extends Page {
  @Override
  final String pageTitle() {
    return "Objectos Way In A Single File #006";
  }

  @Override
  final void renderBody() {
    main(
        dataFrame("main", "home"),

        h1("This website is built entirely using Java"),

        p("It's the Objectos Way!"),

        h2("Pages"),

        ul(
            li(
                a(
                    dataOnClick(Script::navigate),
                    href("/objectos/html?name=Objectos+Way"),
                    text("Objectos HTML")
                )
            ),

            li(
                a(
                    dataOnClick(Script::navigate),
                    href("/objectos/script"),
                    text("Objectos Script")
                )
            )
        )
    );
  }
}

/**
 * The home page "controller".
 *
 * <p>
 * We're using the term "controller" in quotes because Objectos Way itself does
 * not introduce the concept of a "controller", nor does it impose the concept
 * to developers. Case in point, this is a regular method which is used as a
 * method reference in the route declaration. On the other hand, the term (and
 * the concept of a) "controller" is widely used in web development, and we
 * chose to use it here; as this single-file application is intended to be an
 * introduction to the Objectos Way library.
 */
private void home(Http.Exchange http) {
  final Home view;
  view = new Home();

  http.ok(view);
}

/**
 * Renders the Objectos HTML demo of our application.
 */
private static final class ObjectosHtml extends Page {
  private final String name;

  private final boolean show;

  private final int count;

  ObjectosHtml(String name, boolean show, int count) {
    this.name = name;

    this.show = show;

    this.count = count;
  }

  @Override
  final String pageTitle() {
    return "Objectos Way In A Single File #006";
  }

  @Override
  final void renderBody() {
    main(
        dataFrame("main", "html"),

        a(
            dataOnClick(Script::navigate),
            href("/"),
            text("Back")
        ),

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

/**
 * Renders the Objectos Script demo of our application.
 */
private static final class ObjectosScript extends Page {
  @Override
  final String pageTitle() {
    return "Objectos Script";
  }

  @Override
  final void renderBody() {
    main(
        dataFrame("main", "script"),

        a(
            dataOnClick(Script::navigate),
            href("/"),
            text("Back")
        ),

        h1("This page showcases Objectos Script"),

        p("Codes like a server-side rendered app, works like a SPA")
    );
  }
}

/**
 * The Objectos Script "controller".
 */
private void objectosScript(Http.Exchange http) {
  final ObjectosScript view;
  view = new ObjectosScript();

  http.ok(view);
}

/**
 * The {@code styles.css} "controller".
 */
private void styles(Http.Exchange http) {
  final Css.StyleSheet styles;
  styles = Css.StyleSheet.create(opts -> {
    // We should reuse the note sink created during bootstrap.
    // But, that'd require (ideally) the App.Injector class from Objectos Way,
    // which we'll only introduce during a later iteration.
    // So, for now, we just create a new instance here.
    final Note.Sink noteSink;
    noteSink = App.NoteSink.OfConsole.create();

    opts.noteSink(noteSink);

    opts.scanClass(Page.class);

    opts.theme("""
    --color-bg: var(--color-gray-100);
    --color-fg: var(--color-gray-900);
    """);

    opts.theme("@media (prefers-color-scheme: dark)", """
    --color-bg: var(--color-gray-900);
    --color-fg: var(--color-gray-100);
    """);
  });

  http.ok(styles);
}
