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
package org.eolang.parser;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.cactoos.Input;
import org.cactoos.Output;
import org.cactoos.io.InputOf;
import org.cactoos.io.TeeInput;
import org.cactoos.io.UncheckedInput;
import org.cactoos.scalar.LengthOf;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.TextOf;

import java.io.IOException;

/**
 * Syntax parser, from plain text to XML using ANTLR4.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Syntax {

    /**
     * The name of it.
     */
    private final String name;

    /**
     * Text to parse.
     */
    private final Input input;

    /**
     * Target to save XML to.
     */
    private final Output target;

    /**
     * Ctor.
     *
     * @param nme The name of it
     * @param ipt Input text
     * @param tgt Target
     */
    public Syntax(final String nme, final Input ipt, final Output tgt) {
        this.name = nme;
        this.input = ipt;
        this.target = tgt;
    }

    /**
     * Compile it to XML and save.
     *
     * @throws IOException If fails
     */
    public void parse() throws IOException {
        final String[] lines = new TextOf(this.input).asString().split("\n");
        final ANTLRErrorListener errors = new BaseErrorListener() {
            // @checkstyle ParameterNumberCheck (10 lines)
            @Override
            public void syntaxError(final Recognizer<?, ?> recognizer,
                final Object symbol, final int line,
                final int position, final String msg,
                final RecognitionException error) {
                throw new ParsingException(
                    String.format(
                        "[%d:%d] %s: \"%s\"",
                        line, position, msg,
                        // @checkstyle AvoidInlineConditionalsCheck (1 line)
                        lines.length < line ? "EOF" : lines[line - 1]
                    ),
                    error
                );
            }
        };
        final ProgramLexer lexer = new ProgramLexer(
            CharStreams.fromStream(
                new UncheckedInput(this.input).stream()
            )
        );
        lexer.removeErrorListeners();
        lexer.addErrorListener(errors);
        final ProgramParser parser = new ProgramParser(
            new CommonTokenStream(lexer)
        );
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        final XeListener xel = new XeListener(this.name);
        new ParseTreeWalker().walk(xel, parser.program());
        final XML dom = xel.xml();
        Logger.debug(this, "Raw XML:\n%s", dom.toString());
        new Unchecked<>(
            new LengthOf(
                new TeeInput(
                    new InputOf(dom.toString()),
                    this.target
                )
            )
        ).value();
        Logger.debug(this, "Input of %d EO lines compiled", lines.length);
    }

}
