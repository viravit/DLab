/*
 * Copyright (c) 2017, EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.dlab.mongo;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Serialization/Deserialization modul for {@link java.util.Date} that uses {@link IsoDateDeSerializer} and
 * {@link IsoDateSerializer}
 */
public class IsoDateModule extends SimpleModule {
	private static final long serialVersionUID = -2103066255354028256L;

	public IsoDateModule() {
		super();
		addDeserializer(Date.class, new IsoDateDeSerializer());
		addSerializer(Date.class, new IsoDateSerializer());
		addSerializer(LocalDate.class, new IsoLocalDateSerializer());
		addDeserializer(LocalDate.class, new IsoLocalDateDeSerializer());
		addSerializer(LocalTime.class, new ToStringSerializer(LocalTime.class));
		addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
		addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));
		addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);

	}
}
