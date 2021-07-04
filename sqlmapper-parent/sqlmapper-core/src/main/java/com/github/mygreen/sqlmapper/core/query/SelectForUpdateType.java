/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.github.mygreen.sqlmapper.core.query;


/**
 * SELECT ～ FOR UPDATEのタイプを表す列挙です。
 *
 * @author koichik
 */
public enum SelectForUpdateType {

    /**
     * ロックが獲得できるまで待機することを示します。
     */
    NORMAL,

    /**
     * ロックが獲得できない場合に待機しないことを示します。
     */
    NOWAIT,

    /**
     * ロックが獲得できるまで指定時間待機することを示します。
     */
    WAIT
    ;

}
