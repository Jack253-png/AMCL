package org.jnbt.tags;

//@formatter:off

/*
 * JNBT License
 * 
 * Copyright (c) 2010 Graham Edgecombe
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *       
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *       
 *     * Neither the name of the JNBT team nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */

//@formatter:on

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The <code>TAG_Compound</code> tag.
 * 
 * @author Graham Edgecombe
 * 
 */
public final class CompoundTag extends Tag {
	
	/**
	 * The value.
	 */
	private final Map<String, Tag> value;
	
	/**
	 * Creates the tag.
	 * 
	 * @param name
	 *            The name.
	 * @param value
	 *            The value.
	 */
	public CompoundTag(final String name, final Map<String, Tag> value) {
		super(name);
		this.value = Collections.unmodifiableMap(value);
	}
	
	@Override
	public Map<String, Tag> getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.create()
				.toJson(getMap());
	}
	public Map<String, Object> toNativeType(){
		return getMap();
	}

	public Map<String, Object> getMap(){
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, Tag> entry : value.entrySet()){
			if (entry.getValue() instanceof CompoundTag){
				result.put(entry.getKey(), entry.getValue().toTag(CompoundTag.class).getMap());
			}
			else if (!(entry.getValue() instanceof EndTag)){
				if (entry.getValue() instanceof ListTag){
					result.put(entry.getKey(), handleListTag(entry.getValue()));
				}
				else {
					result.put(entry.getKey(), entry.getValue().toNativeType());
				}
			}
		}
		return result;
	}
	public List<Object> handleListTag(Tag tag){
		List<Tag> tags = tag.toTag(ListTag.class).getValue();
		List<Object> result0 = new Vector<>();
		tags.forEach(e -> {
			if (e instanceof CompoundTag){
				result0.add(e.toTag(CompoundTag.class).getMap());
			}
			else if (e instanceof ListTag){
				result0.addAll(handleListTag(e));
			}
			else {
				result0.add(e.toNativeType());
			}
		});
		return result0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
	
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		if (!(obj instanceof CompoundTag)) { return false; }
		final CompoundTag other = (CompoundTag) obj;
		if (value == null) {
			if (other.value != null) { return false; }
		} else if (!value.equals(other.value)) { return false; }
		return true;
	}
	
}
