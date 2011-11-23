// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package test.Ice.invoke;
import java.io.PrintWriter;

import test.Ice.invoke.Test.MyClassPrx;
import test.Ice.invoke.Test.MyClassPrxHelper;
import test.Ice.invoke.Test.MyException;

public class AllTests
{
    final static String testString = "This is a test string";

    private static void
    test(boolean b)
    {
        if(!b)
        {
            throw new RuntimeException();
        }
    }

    private static class Callback
    {
        Callback()
        {
            _called = false;
        }

        public synchronized void check()
        {
            while(!_called)
            {
                try
                {
                    wait();
                }
                catch(InterruptedException ex)
                {
                }
            }

            _called = false;
        }

        public synchronized void called()
        {
            assert(!_called);
            _called = true;
            notify();
        }

        private boolean _called;
    }

    private static class opStringI extends Ice.AsyncCallback
    {
        public opStringI(Ice.Communicator communicator)
        {
            _communicator = communicator;
        }

        @Override
        public void completed(Ice.AsyncResult result)
        {
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(result.getProxy().end_ice_invoke(outParams, result))
            {
                Ice.InputStream inS = Ice.Util.createInputStream(_communicator, outParams.value);
                String s = inS.readString();
                test(s.equals(testString));
                s = inS.readString();
                test(s.equals(testString));
                callback.called();
            }
            else
            {
                test(false);
            }
        }

        public void check()
        {
            callback.check();
        }

        private Ice.Communicator _communicator;
        private Callback callback = new Callback();
    }

    private static class opExceptionI extends Ice.AsyncCallback
    {
        public opExceptionI(Ice.Communicator communicator)
        {
            _communicator = communicator;
        }

        @Override
        public void completed(Ice.AsyncResult result)
        {
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(result.getProxy().end_ice_invoke(outParams, result))
            {
                test(false);
            }
            else
            {
                Ice.InputStream inS = Ice.Util.createInputStream(_communicator, outParams.value);
                try
                {
                    inS.throwException();
                }
                catch(MyException ex)
                {
                    callback.called();
                }
                catch(java.lang.Exception ex)
                {
                    test(false);
                }
            }
        }

        public void check()
        {
            callback.check();
        }

        private Ice.Communicator _communicator;
        private Callback callback = new Callback();
    }

    private static class Callback_Object_opStringI extends Ice.Callback_Object_ice_invoke
    {
        public Callback_Object_opStringI(Ice.Communicator communicator)
        {
            _communicator = communicator;
        }

        @Override
        public void response(boolean ok, byte[] outParams)
        {
            if(ok)
            {
                Ice.InputStream inS = Ice.Util.createInputStream(_communicator, outParams);
                String s = inS.readString();
                test(s.equals(testString));
                s = inS.readString();
                test(s.equals(testString));
                callback.called();
            }
            else
            {
                test(false);
            }
        }

        @Override
        public void exception(Ice.LocalException ex)
        {
            test(false);
        }

        public void check()
        {
            callback.check();
        }

        private Ice.Communicator _communicator;
        private Callback callback = new Callback();
    }

    private static class Callback_Object_opExceptionI extends Ice.Callback_Object_ice_invoke
    {
        public Callback_Object_opExceptionI(Ice.Communicator communicator)
        {
            _communicator = communicator;
        }

        @Override
        public void response(boolean ok, byte[] outParams)
        {
            if(ok)
            {
                test(false);
            }
            else
            {
                Ice.InputStream inS = Ice.Util.createInputStream(_communicator, outParams);
                try
                {
                    inS.throwException();
                }
                catch(MyException ex)
                {
                    callback.called();
                }
                catch(java.lang.Exception ex)
                {
                    test(false);
                }
            }
        }

        @Override
        public void exception(Ice.LocalException ex)
        {
            test(false);
        }

        public void check()
        {
            callback.check();
        }

        private Ice.Communicator _communicator;
        private Callback callback = new Callback();
    }

    public static MyClassPrx
    allTests(Ice.Communicator communicator, PrintWriter out)
    {
        String ref = "test:default -p 12010";
        Ice.ObjectPrx base = communicator.stringToProxy(ref);
        MyClassPrx cl = MyClassPrxHelper.checkedCast(base);
        MyClassPrx oneway = MyClassPrxHelper.uncheckedCast(cl.ice_oneway());

        out.print("testing ice_invoke... ");
        out.flush();

        {
            if(!oneway.ice_invoke("opOneway", Ice.OperationMode.Normal, null, null))
            {
                test(false);
            }

            Ice.OutputStream outS = Ice.Util.createOutputStream(communicator);
            outS.writeString(testString);
            byte[] inParams = outS.finished();
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(cl.ice_invoke("opString", Ice.OperationMode.Normal, inParams, outParams))
            {
                Ice.InputStream inS = Ice.Util.createInputStream(communicator, outParams.value);
                String s = inS.readString();
                test(s.equals(testString));
                s = inS.readString();
                test(s.equals(testString));
            }
            else
            {
                test(false);
            }
        }

        {
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(cl.ice_invoke("opException", Ice.OperationMode.Normal, null, outParams))
            {
                test(false);
            }
            else
            {
                Ice.InputStream inS = Ice.Util.createInputStream(communicator, outParams.value);
                try
                {
                    inS.throwException();
                }
                catch(MyException ex)
                {
                }
                catch(java.lang.Exception ex)
                {
                    test(false);
                }
            }
        }

        out.println("ok");

        out.print("testing asynchronous ice_invoke... ");
        out.flush();

        {
            Ice.AsyncResult result = oneway.begin_ice_invoke("opOneway", Ice.OperationMode.Normal, null);
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(!oneway.end_ice_invoke(outParams, result))
            {
                test(false);
            }

            Ice.OutputStream outS = Ice.Util.createOutputStream(communicator);
            outS.writeString(testString);
            byte[] inParams = outS.finished();

            // begin_ice_invoke with no callback
            result = cl.begin_ice_invoke("opString", Ice.OperationMode.Normal, inParams);
            if(cl.end_ice_invoke(outParams, result))
            {
                Ice.InputStream inS = Ice.Util.createInputStream(communicator, outParams.value);
                String s = inS.readString();
                test(s.equals(testString));
                s = inS.readString();
                test(s.equals(testString));
            }
            else
            {
                test(false);
            }

            // begin_ice_invoke with Callback
            opStringI cb1 = new opStringI(communicator);
            cl.begin_ice_invoke("opString", Ice.OperationMode.Normal, inParams, cb1);
            cb1.check();

            // begin_ice_invoke with Callback_Object_ice_invoke
            Callback_Object_opStringI cb2 = new Callback_Object_opStringI(communicator);
            cl.begin_ice_invoke("opString", Ice.OperationMode.Normal, inParams, cb2);
            cb2.check();
        }

        {
            // begin_ice_invoke with no callback
            Ice.AsyncResult result = cl.begin_ice_invoke("opException", Ice.OperationMode.Normal, null);
            Ice.ByteSeqHolder outParams = new Ice.ByteSeqHolder();
            if(cl.end_ice_invoke(outParams, result))
            {
                test(false);
            }
            else
            {
                Ice.InputStream inS = Ice.Util.createInputStream(communicator, outParams.value);
                try
                {
                    inS.throwException();
                }
                catch(MyException ex)
                {
                }
                catch(java.lang.Exception ex)
                {
                    test(false);
                }
            }

            // begin_ice_invoke with Callback
            opExceptionI cb1 = new opExceptionI(communicator);
            cl.begin_ice_invoke("opException", Ice.OperationMode.Normal, null, cb1);
            cb1.check();

            // begin_ice_invoke with Callback_Object_ice_invoke
            Callback_Object_opExceptionI cb2 = new Callback_Object_opExceptionI(communicator);
            cl.begin_ice_invoke("opException", Ice.OperationMode.Normal, null, cb2);
            cb2.check();
        }

        out.println("ok");

        return cl;
    }
}
