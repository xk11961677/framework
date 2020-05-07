/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.kv;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * {@link IndexedPeekIterator} is an Iterator which provides user a
 * {@link #peek()} method to peek an element advanced, a {@link #getIndex()}
 * method to get the index of last returned element and a {@link #getCurrent()}
 * method to get the last returned element itself.
 * 
 * @param <E>
 *          the type of elements
 * 
 * @author
 * 
 */
public final class IndexedPeekIterator<E> implements Iterator<E> {

  /**
   * Creates an {@link IndexedPeekIterator} by given Iterable.
   * 
   * @param <T>
   *          the type of elements
   * @param iter
   *          any Iterable
   * @return an {@link IndexedPeekIterator}
   */
  public static <T> IndexedPeekIterator<T> newIndexedPeekIterator(
      Iterable<T> iter) {
    return new IndexedPeekIterator<T>(iter.iterator());
  }

  private final Iterator<? extends E> iterator;
  private E peek;
  private boolean hasPeek = false;
  private int index = -1;
  private E current = null;

  /**
   * Creates an {@link IndexedPeekIterator}.
   * 
   * @param iterator
   *          an Iterator
   */
  public IndexedPeekIterator(Iterator<? extends E> iterator) {
    if (iterator == null) {
      throw new NullPointerException();
    }

    this.iterator = iterator;
  }

  private void peeking() {
    peek = iterator.next();
    hasPeek = true;
  }

  /**
   * Returns the index of last returned element. If there is no element has been
   * returned, it returns -1.
   * 
   * @return the index of last returned element
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the last returned element. If {@link #next()} has never been
   * called, it returns null.
   * 
   * @return the last returned element
   */
  public E getCurrent() {
    return current;
  }

  @Override
  public boolean hasNext() {
    return hasPeek || iterator.hasNext();
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    index++;
    if (hasPeek) {
      hasPeek = false;
      current = peek;
      return current;
    } else {
      peeking();
      return next();
    }
  }

  @Override
  public void remove() {
    if (hasPeek) {
      throw new IllegalStateException();
    }

    iterator.remove();
  }

  /**
   * Peeks an element advanced. Warning: remove() is temporarily out of function
   * after a peek() until a next() is called.
   * 
   * @return element
   */
  public E peek() {
    if (!hasPeek && hasNext()) {
      peeking();
    }
    if (!hasPeek) {
      throw new NoSuchElementException();
    }

    return peek;
  }

}
