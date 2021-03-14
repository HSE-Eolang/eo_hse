/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2021 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.io;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.eolang.phi.Data;
import org.eolang.phi.Dataized;
import org.eolang.phi.PhCopy;
import org.eolang.phi.PhMethod;
import org.eolang.phi.PhEta;
import org.eolang.phi.Phi;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

/**
 * Test case for {@link EOstdin}.
 *
 * @since 0.1
 */
public final class EOstdinTest {

    private static final InputStream DEFAULT_STDIN = System.in;
 
    @AfterAll
    public static void rollbackChangesToStdin() {
        System.setIn(DEFAULT_STDIN);
    }
 
    @Test
    public void nextLineOneLineTest() throws Exception {
        String expectedResult = "this is a test input!";
        mockSystemIn(expectedResult + "\n");

        final Phi nextLineCopy = new PhMethod(new PhCopy(new EOstdin(new PhEta())), "nextLine");
        final String gatheredInput = new Dataized(nextLineCopy).take(String.class);
        MatcherAssert.assertThat(
            gatheredInput,
            Matchers.equalTo(expectedResult)
        );
    }

    @Test
    public void nextLineMultiLineTest() throws Exception {
        String expectedResult = "this is a test input!";
        String input = expectedResult + "\nanother line\nyet another line";
        mockSystemIn(input);

        final Phi nextLineCopy = new PhMethod(new PhCopy(new EOstdin(new PhEta())), "nextLine");
        final String gatheredInput = new Dataized(nextLineCopy).take(String.class);
        MatcherAssert.assertThat(
            gatheredInput,
            Matchers.equalTo(expectedResult)
        );
    }

    @Test
    public void nextLineEmptyTest() throws Exception {
        String input = "";
        mockSystemIn(input);
        final Phi result = new PhMethod(new PhCopy(new EOstdin(new PhEta())), "nextLine");
        MatcherAssert.assertThat(
            new Dataized(
                result.attr("msg").get()
            ).take(String.class),
            Matchers.equalTo("There is no line in the standard input stream to consume")
        );
        Assertions.assertThrows(org.eolang.phi.Attr.Exception.class, () -> {
            new Dataized(result).take(String.class);
        });
    }

    @Test
    public void stdinOneLineTest() throws Exception {
        String input = "this is a test input!\n";
        mockSystemIn(input);

        final Phi stdinCopy = new PhCopy(new EOstdin(new PhEta()));
        final String gatheredInput = new Dataized(stdinCopy).take(String.class);
        MatcherAssert.assertThat(
            gatheredInput,
            Matchers.equalTo(input)
        );
    }

    @Test
    public void stdinMultiLineTest() throws Exception {
        String input = "this is a test input!\nanother line\nyet another line";
        mockSystemIn(input);

        final Phi stdinCopy = new PhCopy(new EOstdin(new PhEta()));
        final String gatheredInput = new Dataized(stdinCopy).take(String.class);
        MatcherAssert.assertThat(
            gatheredInput,
            Matchers.equalTo(input)
        );
    }

    @Test
    public void stdinEmptyTest() throws Exception {
        String input = "";
        mockSystemIn(input);

        final Phi stdinCopy = new PhCopy(new EOstdin(new PhEta()));
        final String gatheredInput = new Dataized(stdinCopy).take(String.class);
        MatcherAssert.assertThat(
            gatheredInput,
            Matchers.equalTo(input)
        );
    }

    private void mockSystemIn(String mockingText) {
        System.setIn(new ByteArrayInputStream(mockingText.getBytes()));
    }
}
