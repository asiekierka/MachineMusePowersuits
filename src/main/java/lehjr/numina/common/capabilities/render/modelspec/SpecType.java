/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lehjr.numina.common.capabilities.render.modelspec;

import java.util.Arrays;

public enum SpecType {
    // Obj models rendered as armor
    ARMOR_OBJ_MODEL("ARMOR_OBJ_MODEL"),

    // pretty much what Minecraft already uses
    ARMOR_SKIN("ARMOR_SKIN"),

    // model intended to render in the hand
    HANDHELD_OBJ_MODEL("HANDHELD_OBJ"),

    // java based model that renderes in the hand
    HANDHELD_JAVA_MODEL("HANDHELD_JAVA_MODEL"),
    NONE("NONE");

    String name;

    SpecType(String name) {
        this.name = name;
    }

    public static SpecType getTypeFromName(String nameIn) {
        String finalNameIn = nameIn.toUpperCase().replaceAll("\\s", "");
        return Arrays.stream(values()).filter(spec -> spec.getName().equals(finalNameIn)).findAny().orElse(null);
    }

    public String getName() {
        return this.name;
    }
}
