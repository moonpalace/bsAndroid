//package com.example.noircynical.bs;
//
//import java.util.HashMap;
//
//public class S<T> implements IS {
//
//    static private Run<P> run = new Run<P>() {
//        public void run(P $p) {
//            ((S) $p.get(BS.HANDLER.id)).s($p.get("arg"));
//        }
//    };
//
//    public P p;
//    public HashMap<Object, Iattr<T>> fn;
//
//    public T self;
//
//    public S(T $self, HashMap<Object, Iattr<T>> $fn, Object[] $arg) {
//        self = $self == null ? (T) this : $self;
//        fn = $fn;
//        p = P.pool();
//        if ($arg != null) {
//            int i = 0, j = $arg.length;
//            while (i < j) p.put($arg[i++], $arg[i++]);
//        }
//    }
//
//    protected void _d() {
//    }
//
//    public void d() {
//        P.pool(p);
//        _d();
//    }
//
//    public Object s(Object... $arg) {
//        if ($arg == null) {
//            d();
//            return null;
//        }
//        int j = $arg.length;
//        if (j == 1 && $arg[0] instanceof Object[]) {
//            $arg = (Object[]) $arg[0];
//            j = $arg.length;
//        }
//        if ($arg[0] == null) {
//            d();
//            return null;
//        } else {
//            Object t0 = null;
//            int i = 0;
//            while (i < j) {
//                Object k = $arg[i++], v;
//                if (i == j) {
//                    if (p.containsKey(k)) t0 = p.get(k);
//                    else if (fn.containsKey(k)) t0 = fn.get(k).g(self);
//                } else {
//                    v = $arg[i++];
//                    if (fn.containsKey(k)) t0 = fn.get(k).s(self, v);
//                    else {
//                        p.put(k, v);
//                        t0 = v;
//                    }
//                }
//            }
//            return t0;
//        }
//    }
//
//    public Object a(BS $bs, Object... $arg) {
//        $bs.sendMessage(run, this, "arg", $arg);
//        return null;
//    }
//
//}