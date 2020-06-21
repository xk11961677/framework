/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rule.engine.component;

import com.sky.framework.rule.engine.component.command.OperatorCommand;
import com.sky.framework.rule.engine.util.SpiLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
@Slf4j
public class CommandTable {

    private static final Map<String, OperatorCommand> container = new ConcurrentHashMap<>();

    public static final CommandTable INSTANCE = new CommandTable();

    private CommandTable() {
        ServiceLoader<OperatorCommand> operatorCommands = SpiLoader.loadAll(OperatorCommand.class);
        Iterator<OperatorCommand> iterator = operatorCommands.iterator();
        while (iterator.hasNext()) {
            OperatorCommand command = iterator.next();
            container.put(command.operator(), command);
        }
    }

    public OperatorCommand get(String key) {
        return container.get(key);
    }

    public void put(String key, OperatorCommand command) {
        container.put(key, command);
    }
}
