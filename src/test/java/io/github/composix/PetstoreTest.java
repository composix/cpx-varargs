/**
 * MIT License
 *
 * Copyright (c) 2025 ComPosiX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.composix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import io.github.composix.math.Args;
import io.github.composix.math.MutableOrder;
import io.github.composix.math.Ordinal;
import io.github.composix.models.examples.Order;
import io.github.composix.models.examples.Pet;
import io.github.composix.testing.TestCase;
import io.github.composix.testing.TestData;
import io.github.composix.varargs.ArgsI;

@WireMockTest
class PetstoreTest extends TestCase {
    static final String PETSTORE_API = "https://petstore3.swagger.io/api/v3/openapi.json";

    TestData testData;

    @BeforeEach
    void beforeEach(WireMockRuntimeInfo wm) throws IOException {
        testData = testData(wm, PETSTORE_API)
            .select("~","pet","findByStatus","?status","=available")
            .refresh(Pet.class);

        testData
            .select("~","store","order",":orderId","=10")
            .refresh(Order.class);
    }

    @Test
    void testPetstore() {
        final MutableOrder order = Ordinal.D.order();
        order.reorder(B, C);
        final Args pets =
            ArgsI.of(
                testData
                    .select("~","pet")
                    .collect()
                .toArray(Pet[]::new))
                    .select(B, Pet::name)
                    .selectLong(C, Pet::id)
                .select(order);

        final Args orders = Ordinal
            .OMEGA.extendA(
                testData
                    .select("~","store","order")
                    .collect()
                .toArray(Order[]::new))
                    .selectLongB(Order::petId)
                    .selectLongC(Order::quantity)
                .select(order);

        Args orderAmounts = pets.join(orders);

        final Ordinal A = Ordinal.A, B = Ordinal.B, C = Ordinal.C;
        assertEquals("Dog_Pluto", orderAmounts.getValue(A.index(A)));
        assertEquals(101L, orderAmounts.getLongValue(B.index(A)));
        assertEquals(7L, orderAmounts.getLongValue(C.index(A)));        
    }
}
