package org.eolang.txt;

import org.eolang.core.EOObject;
import org.eolang.core.data.EOData;

import java.util.Arrays;

/**
 * Объект осуществляющий создание форматированых строк.
 */
public class EOsprintf extends EOObject {
    private EOObject format;
    private EOObject[] data;

    public EOsprintf(EOObject format, EOObject... data) {
        this.format = format;
        this.data = data;
    }

    @Override
    public EOData _getData() {
        String sFormat = format._getData().toString();
        Object[] objects = Arrays.stream(data).map(obj -> obj._getData().toObject()).toArray();
        return new EOData(
                String.format(
                        sFormat,
                        objects
                )
        );
    }
}
