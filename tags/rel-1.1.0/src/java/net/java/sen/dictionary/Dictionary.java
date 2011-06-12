/*
 * Copyright (C) 2002-2007
 * Taku Kudoh <taku-ku@is.aist-nara.ac.jp>
 * Takashi Okamoto <tora@debian.org>
 * Matt Francis <asbel@neosheffield.co.uk>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package net.java.sen.dictionary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import net.java.sen.SenFactory;
import net.java.sen.trie.CharIterator;
import net.java.sen.trie.TrieSearcher;

/**
 * The <code>Dictionary</code> class wraps access to a compiled Sen dictionary
 */
public class Dictionary {
  
  /** Mapped buffer of the token file (token.sen) */
  private final ByteBuffer tokenBuffer;
  
  /** Mapped buffer of the part-of-speech information file (partOfSpeech.sen) */
  private final ByteBuffer partOfSpeechInfoBuffer;
  
  /** Mapped buffer of the Trie data (trie.sen) */
  private final IntBuffer trieBuffer;
  
  /** Mapped buffer of the connection cost matrix file (connectionCost.sen) */
  private final ShortBuffer connectionCostBuffer;
  
  /** Size of the first extent of the connection cost matrix */
  private final int connectionSize1;
  
  /** Size of the second extent of the connection cost matrix */
  private final int connectionSize2;
  
  /** Size of the third extent of the connection cost matrix */
  private final int connectionSize3;
  
  /** A CToken representing a beginning-of-string */
  private final CToken bosToken;
  
  /** A CToken representing an end-of-string */
  private final CToken eosToken;
  
  /** A CToken representing an unknown morpheme */
  private final CToken unknownToken;
  
  /**
   * A buffer used to store result indices from a Trie search. Reused on
   * every call to the {@link #commonPrefixSearch(CharIterator)} method
   */
  private final int trieSearchResults[] = new int[256];
  
  /**
   * A buffer used to store {@link CToken}s resulting from a search. Reused
   * on every call to the {@link #commonPrefixSearch(CharIterator)} method
   */
  private final CToken results[] = new CToken[256];
  
  final String posIndex[];
  final String conjTypeIndex[];
  final String conjFormIndex[];
  
  /**
   * Gets a unique beginning-of-string {@link CToken <code>CToken</code>}. The {@link CToken <code>CToken</code>} returned by this method is
   * freshly cloned and not an alias of any other {@link CToken <code>CToken</code>}
   *
   * @return A beginning-of-string CToken
   */
  public CToken getBOSToken() {
    return bosToken.clone();
  }
  
  /**
   * Gets a unique end-of-string {@link CToken <code>CToken</code>}. The {@link CToken <code>CToken</code>} returned by this method is
   * freshly cloned and not an alias of any other {@link CToken <code>CToken</code>}
   *
   * @return An end-of-string CToken
   */
  public CToken getEOSToken() {
    return eosToken.clone();
  }
  
  /**
   * Gets a unique unknown-morpheme {@link CToken <code>CToken</code>}. The {@link CToken <code>CToken</code>} returned by this method is
   * freshly cloned and not an alias of any other {@link CToken <code>CToken</code>}
   *
   * @return A unknown-morpheme CToken
   */
  public CToken getUnknownToken() {
    return unknownToken.clone();
  }
  
  /**
   * Returns the part of speech info character buffer
   *
   * @return The character buffer
   */
  ByteBuffer getPartOfSpeechInfoBuffer() {
    return partOfSpeechInfoBuffer;
  }
  
  /**
   * Retrieves the cost between three Nodes from the connection cost matrix
   * 
   * @param lNode2 The first Node
   * @param lNode The second Node
   * @param rNode The third Node
   * @return The connection cost
   */
  public int getCost(Node lNode2, Node lNode, Node rNode) {
    final int position = connectionSize3 * (connectionSize2 * lNode2.ctoken.rcAttr2 + lNode.ctoken.rcAttr1) + rNode.ctoken.lcAttr;
    return connectionCostBuffer.get(position) + rNode.ctoken.cost;
  }
  
  /**
   * Searches for possible morphemes starting at the current position of a
   * CharIterator. The iterator is advanced by the length of the longest
   * matching morpheme 
   *
   * @param iterator The iterator to search from 
   * @return The possible morphemes found
   */
  public CToken[] commonPrefixSearch(CharIterator iterator) {
    int size = 0;
    
    int n = TrieSearcher.commonPrefixSearch(trieBuffer, iterator, trieSearchResults);
    
    for (int i = 0; i < n; i++) {
      int k = trieSearchResults[i] & 0xff;
      int p = trieSearchResults[i] >> 8;
    
    tokenBuffer.position((int) ((p + 3) * CToken.SIZE));
    for (int j = 0; j < k; j++) {
      results[size++] = CToken.read(tokenBuffer);
    }
    }
    
    // Null terminate
    results[size] = null;
    
    return results;
  }
  
  /**
   * @throws IOException
   */
  public Dictionary() {
    // Map connection cost file
    ShortBuffer buffer = SenFactory.getConnectionCostBuffer();
    
    connectionSize1 = buffer.get();
    connectionSize2 = buffer.get();
    connectionSize3 = buffer.get();
    
    int expectedSize = 3 + (connectionSize1 * connectionSize2 * connectionSize3);
    if (expectedSize != buffer.limit()) {
      throw new RuntimeException("Expected connection cost file to be " + (2 * expectedSize) + " bytes, but was " + (2 * buffer.limit()));
    }
    
    this.connectionCostBuffer = buffer.slice();
    
    // Map position infomation file.
    this.partOfSpeechInfoBuffer = SenFactory.getPOSBuffer();
    
    // Map token file
    this.tokenBuffer = SenFactory.getTokenBuffer();
    this.bosToken = CToken.read(this.tokenBuffer);
    this.eosToken = CToken.read(this.tokenBuffer);
    this.unknownToken = CToken.read(this.tokenBuffer);
    
    // Map double array trie dictionary
    this.trieBuffer = SenFactory.getTrieBuffer();
    
    // indexes (unique POS values, etc)
    this.posIndex = SenFactory.getPOSIndex();
    this.conjTypeIndex = SenFactory.getConjTypeIndex();
    this.conjFormIndex = SenFactory.getConjFormIndex();
  }
}
