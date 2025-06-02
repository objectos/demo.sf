
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

static class Home extends Html.Template {
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
 * Registers the routes of the application
 */
private void routes(Http.Routing routing) {
  routing.path("/", path -> {
    path.allow(Http.Method.GET, http -> {
      http.ok(new Home());
    });
  });

  routing.handler(http -> {
    http.notFound(Media.Bytes.textPlain("Not Found"));
  });
}