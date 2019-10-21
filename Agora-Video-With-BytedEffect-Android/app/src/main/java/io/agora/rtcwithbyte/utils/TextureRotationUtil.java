/*
 * Copyright (C) 2012 CyberAgent
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
package io.agora.rtcwithbyte.utils;

public class TextureRotationUtil {


    /**
     * 上下翻转
     * 顶点标号为
     * 1  2
     * 3  4
     */

    public static final float TEXTURE_FLIPPED[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    /**
     * 顺时针旋转0度
     * 顶点标号为
     * 3  4
     * 1  2
     */
    public static final float TEXTURE_ROTATED_0[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };
    /**
     * 顺时针旋转90度
     * 顶点标号为
     * 1  3
     * 2  4
     */
    public static final float TEXTURE_ROTATED_90[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    /**
     * 顺时针旋转180
     * 顶点标号为
     * 2  1
     * 4  3
     */
    public static final float TEXTURE_ROTATED_180[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };
    /**
     * 顶点标号为
     * 4  2
     * 3  1
     */
    public static final float TEXTURE_ROTATED_270[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    /**
     *顶点坐标标号
     * 3 4
     * 1 2
     */
    public static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };



    private TextureRotationUtil() {
    }

    public static float[] getRotation(final int rotation, boolean flipHorizontal,
                                      boolean flipVertical) {
        float[] rotatedTex;
        boolean temp;
        switch (rotation) {
            case 90:
                rotatedTex = TEXTURE_ROTATED_90;
                temp = flipHorizontal;
                flipHorizontal = flipVertical;
                flipVertical = temp;
                break;
            case 180:
                rotatedTex = TEXTURE_ROTATED_180;
                break;
            case 270:
                rotatedTex = TEXTURE_ROTATED_270;
                temp = flipHorizontal;
                flipHorizontal = flipVertical;
                flipVertical = temp;
                break;
            case 0:
            case 360:
            default:
                rotatedTex = TEXTURE_ROTATED_0;
                break;
        }
        if (flipHorizontal) {
            rotatedTex = new float[]{
                    flip(rotatedTex[0]), rotatedTex[1],
                    flip(rotatedTex[2]), rotatedTex[3],
                    flip(rotatedTex[4]), rotatedTex[5],
                    flip(rotatedTex[6]), rotatedTex[7],
            };
        }
        if (flipVertical) {
            rotatedTex = new float[]{
                    rotatedTex[0], flip(rotatedTex[1]),
                    rotatedTex[2], flip(rotatedTex[3]),
                    rotatedTex[4], flip(rotatedTex[5]),
                    rotatedTex[6], flip(rotatedTex[7]),
            };
        }
        return rotatedTex;
    }


    private static float flip(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }
}
