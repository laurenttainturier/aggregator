/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.CacheListener.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator;

/*-
 * #%L
 * aggregator
 * %%
 * Copyright (C) 2019 Bigeon
 * %%
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 * #L%
 */

/** A listener for a cache.
 * <p>
 * Cache listeners will be notified of new data as well as flushes of the cache.
 *
 * @author Emmanuel Bigeon */
public interface CacheListener {
    /** Indicate new data in the cache
     * 
     * @param source the cache source */
    void addedData(Cache source);

    /** The new data source.
     * 
     * @param source */
    void flushed(Cache source);
}
