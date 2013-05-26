/*
 * Copyright (c) 2012, Enhanced Four
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'Enhanced Four' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.indiespot.continuations.util;

import java.io.Serializable;

import net.indiespot.continuations.VirtualThread;

import craterstudio.data.CircularArrayList;

import de.matthiasmann.continuations.*;

public class VirtualExchanger<T> implements Serializable {
	private final VirtualCondition queueNotEmpty;
	private final CircularArrayList<Slot<T>> queue;

	public VirtualExchanger() {
		queueNotEmpty = new VirtualCondition();
		queue = new CircularArrayList<>(1);
	}

	public T exchange(T item) throws SuspendExecution {
		VirtualThread self = VirtualThread.currentThread();

		queue.addLast(new Slot<T>(item));
		queueNotEmpty.signalAll();

		for (;;) {
			while (queue.isEmpty()) {
				queueNotEmpty.await();
			}

			if (queue.peekFirst().producer != self) {
				return queue.pollFirst().value;
			}

			queueNotEmpty.await();
		}
	}

	private static class Slot<T> {
		public final T value;
		public final VirtualThread producer;

		public Slot(T value) {
			this.value = value;
			this.producer = VirtualThread.currentThread();
		}
	}
}