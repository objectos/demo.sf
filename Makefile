#
# Copyright (C) 2025 Objectos Software LTDA.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# SFOW (Single-File Objectos Way)
#

## Coordinates
GROUP_ID := br.com.objectos
ARTIFACT_ID := demo.sfow
VERSION := 001-SNAPSHOT

## Maven interop
CENTRAL := https://repo.maven.apache.org/maven2

## Objectos Way Version
WAY_VERSION := 0.2.2

# Delete the default suffixes
.SUFFIXES:

#
# sfow
#

.PHONY: all
all: run

include make/java-core.mk

#
# sfow@clean
#

include make/common-clean.mk

#
# sfow@run
#

## file name
START := main/Start.java

## Objectos Way JAR
WAY_JAR := $(WORK)/objectos.way-$(WAY_VERSION).jar

## java command
RUNX := $(JAVA)
RUNX += --module-path $(WAY_JAR)
RUNX += --add-modules objectos.way
RUNX += --enable-preview
RUNX += $(START)

## wget command
WGETX := wget
WGETX += --directory-prefix=$(WORK)
WGETX += --no-verbose

.PHONY: run
run: $(WAY_JAR)
	$(RUNX) 

$(WAY_JAR):
	$(WGETX) $(CENTRAL)/br/com/objectos/objectos.way/$(WAY_VERSION)/objectos.way-$(WAY_VERSION).jar

#
# GH secrets
#

## - GH_TOKEN
-include $(HOME)/.config/objectos/gh-config.mk

#
# sfow@gh-release
#

include make/gh-release.mk
