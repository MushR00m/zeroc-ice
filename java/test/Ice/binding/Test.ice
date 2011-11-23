// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef TEST_ICE
#define TEST_ICE

[["java:package:test.Ice.binding"]]
module Test
{

interface TestIntf
{
    string getAdapterName();
};

interface RemoteObjectAdapter
{
    TestIntf* getTestIntf();
    
    void deactivate();
};

interface RemoteCommunicator
{
    RemoteObjectAdapter* createObjectAdapter(string name, string endpoints);

    void deactivateObjectAdapter(RemoteObjectAdapter* adapter);

    void shutdown();
};

};

#endif
