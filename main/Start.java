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

void main() throws java.io.IOException {
  var noteSink = App.NoteSink.OfConsole.create();

  var shutdownHook = App.ShutdownHook.create(opts -> {
    opts.noteSink(noteSink);
  });

  var handler = Http.Handler.create(routing -> {
    routing.path("/", path -> {
      path.allow(Http.Method.GET, http -> {
        http.ok(Media.Bytes.textPlain("It Works!"));
      });
    });

    routing.handler(http -> {
      http.notFound(Media.Bytes.textPlain("Not Found"));
    });
  });

  var server = Http.Server.create(opts -> {
    opts.handler(handler);

    opts.noteSink(noteSink);

    opts.port(8080);
  });

  shutdownHook.register(server);

  server.start();
}