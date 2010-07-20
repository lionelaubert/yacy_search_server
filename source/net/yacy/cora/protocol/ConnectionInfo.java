// HttpConnectionInfo.java
// (C) 2008 by Daniel Raap; danielr@users.berlios.de
// first published 07.04.2008 on http://yacy.net
//
// This is a part of YaCy, a peer-to-peer based web search engine
//
// $LastChangedDate: 2008-03-14 01:16:04 +0100 (Fr, 14 Mrz 2008) $
// $LastChangedRevision: 4558 $
// $LastChangedBy: orbiter $
//
// LICENSE
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package net.yacy.cora.protocol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Information about a connection
 * 
 * @author daniel
 * @author sixcooler
 */
public class ConnectionInfo {
    /**
     * a list of all current connections to be shown in Connections_p
     */
    private final static Set<ConnectionInfo> allConnections = Collections
            .synchronizedSet(new HashSet<ConnectionInfo>());
    // this is only for statistics, so it can be bigger to see lost connectionInfos
    private final static int staleAfterMillis = 30 * 60000; // 30 minutes
    
    private static int maxcount = 20;

    private final String protocol;
    private final String targetHost;
    private final String command;
    private final int id;
    private final long initTime;

    /**
     * constructor setting all data
     * 
     * @param protocol
     * @param targetHost
     * @param command
     * @param id
     * @param initTime
     */
    public ConnectionInfo(final String protocol, final String targetHost, final String command, final int id,
            final long initTime) {
        this.protocol = protocol;
        this.targetHost = targetHost;
        this.command = command;
        this.id = id;
        this.initTime = initTime;
    }

    /**
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return
     */
    public long getLifetime() {
        return System.currentTimeMillis() - initTime;
    }

    /**
     * @return dummy 0
     */
    public int getIdletime() {
        return 0;
    }

    /**
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return
     */
    public String getTargetHost() {
        return targetHost;
    }

    /**
     * @return
     */
    public int getID() {
        return id;
    }

    /**
     * gets a {@link Set} of all collected ConnectionInfos
     * 
     * Important: iterations must be synchronized!
     * 
     * @return the allConnections
     */
    public static Set<ConnectionInfo> getAllConnections() {
        return allConnections;
    }
    
    /**
     * gets the number of active client connections
     * 
     * @return count of active connections
     */
    public static int getCount() {
    	return allConnections.size();
    }
    
    /**
     * gets the usage of the Client connection manager by active connections
     * 
     * @return load in percent
     */
    public static int getLoadPercent() {
    	return getCount() * 100 / maxcount;
    }
    
    /**
     * gets the max connection count of the Client connection manager
     * 
     * @return max connections
     */
    public static int getMaxcount() {
    	return maxcount;
    }
    
    /**
     * gets the max connection count of the Client connection manager
     * to be used in statistics
     * 
     * @param max connections
     */
    protected static void setMaxcount(final int max) {
    	if (max > 0) maxcount = max;
    }

    /**
     * add a connection to the list of all current connections
     * 
     * @param conInfo
     */
    protected static void addConnection(final ConnectionInfo conInfo) {
        allConnections.add(conInfo);
    }

    /**
     * remove a connection from the list of all current connections
     * 
     * @param conInfo
     */
    protected static void removeConnection(final ConnectionInfo conInfo) {
        allConnections.remove(conInfo);
    }

    /**
     * connections with same id {@link equals()} another
     * 
     * @param id
     */
    protected static void removeConnection(final int id) {
        removeConnection(new ConnectionInfo(null, null, null, id, 0));
    }
    
    /**
     * removes stale connections
     */
    public static void cleanUp() {
        try {
            synchronized (allConnections) {
                for(final ConnectionInfo con: allConnections) {
                    if(con.getLifetime() > staleAfterMillis) {
                        allConnections.remove(con);
                    }
                }
            }
        } catch (final java.util.ConcurrentModificationException e) {
            // there will be another try :-)
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder(50);
        string.append("ID ");
        string.append(getID());
        string.append(", ");
        string.append(getProtocol());
        string.append("://");
        string.append(getTargetHost());
        string.append(" ");
        string.append(getCommand());
        string.append(", since ");
        string.append(getLifetime());
        string.append(" ms");
        return string.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)  return true;
        if (obj == null)  return false;
        if (getClass() != obj.getClass()) return false;
        final ConnectionInfo other = (ConnectionInfo) obj;
        return this.id == other.id;
    }
}
