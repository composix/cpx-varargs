package io.github.composix.varargs;

import java.util.function.Function;
import java.util.function.ToLongFunction;

import io.github.composix.math.Matrix;
import io.github.composix.math.Ordinal;

class MatrixI<A> extends Matrix implements ArgsI<A> {

    MatrixI() {
        super();
    }

    public ArgsI<A> clone() throws CloneNotSupportedException {
        return (ArgsI<A>) super.clone();
    }
    
    @Override
    public ArgsI<A> select(Ordinal ordinal, Function<A, ?> accessor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }

    @Override
    public ArgsI<A> selectLong(Ordinal ordinal, ToLongFunction<A> accessor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectLong'");
    }

    @Override
    public <T> ArgsII<A, T> selectB(Function<A, T> accessor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectB'");
    }

    @Override
    public ArgsII<A, long[]> selectLongB(ToLongFunction<A> accessor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectLongB'");
    }
    
}
