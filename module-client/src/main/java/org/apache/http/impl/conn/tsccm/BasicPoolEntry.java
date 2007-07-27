/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.impl.conn.tsccm;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.apache.http.conn.HttpRoute;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.impl.conn.AbstractPoolEntry;



/**
 * Basic implementation of a connection pool entry.
 */
public class BasicPoolEntry extends AbstractPoolEntry {

    /** The connection pool. */
    private AbstractConnPool connPool;

    /** The connection operator. */
    //@@@ move to base class, drop getOperator()?
    private ClientConnectionOperator connOperator;


    /**
     * A weak reference to <code>this</code> used to detect GC of entries.
     * Pool entries can only be GCed when they are allocated by an application
     * and therefore not referenced with a hard link in the manager.
     */
    private BasicPoolEntryRef reference;

   
    /**
     * Creates a new pool entry.
     *
     * @param pool    the connection pool
     * @param op      the connection operator
     * @param route   the planned route for the connection
     * @param queue   the reference queue for tracking GC of this entry,
     *                or <code>null</code>
     */
    public BasicPoolEntry(AbstractConnPool pool,
                          ClientConnectionOperator op,
                          HttpRoute route,
                          ReferenceQueue queue) {
        //@@@ create connection in base? or delay creation until needed?
        super(op.createConnection(), route);
        if (pool == null) {
            throw new IllegalArgumentException
                ("Connection pool must not be null.");
        }
        if (route == null) {
            throw new IllegalArgumentException
                ("Planned route must not be null.");
        }

        this.connPool = pool;
        this.connOperator = op;
        this.reference = new BasicPoolEntryRef(this, queue);
    }


    // non-javadoc, see base AbstractPoolEntry
    protected ClientConnectionOperator getOperator() {
        return this.connOperator;
    }


    protected final OperatedClientConnection getConnection() {
        return super.connection;
    }

    protected final HttpRoute getPlannedRoute() {
        return super.plannedRoute;
    }

    protected final WeakReference getWeakRef() {
        return this.reference;
    }

    protected final AbstractConnPool getConnPool() {
        return this.connPool;
    }


} // class BasicPoolEntry

