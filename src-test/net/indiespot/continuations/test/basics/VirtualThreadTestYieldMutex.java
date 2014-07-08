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

package net.indiespot.continuations.test.basics;

import net.indiespot.continuations.VirtualProcessor;
import net.indiespot.continuations.VirtualRunnable;
import net.indiespot.continuations.VirtualThread;
import net.indiespot.continuations.util.VirtualCondition;
import de.matthiasmann.continuations.*;

public class VirtualThreadTestYieldMutex {
	@SuppressWarnings("serial")
	public static void main(String[] args) {
		VirtualProcessor proc = new VirtualProcessor();

		final VirtualCondition condition = new VirtualCondition();

		VirtualRunnable task = new VirtualRunnable() {
			@Override
			public void run() throws SuspendExecution {
				System.out.println("hello");
				VirtualThread.yield();
				System.out.println("world");
				VirtualThread.yield();
				System.out.println("aight?");
				condition.await();
				System.out.println("aight!");
			}
		};

		VirtualThread thread = new VirtualThread(task);
		thread.start();

		System.out.println("---");
		proc.tick(System.currentTimeMillis());
		System.out.println("---");
		proc.tick(System.currentTimeMillis());
		System.out.println("---");
		proc.tick(System.currentTimeMillis());
		System.out.println("---");
		proc.tick(System.currentTimeMillis());
		System.out.println("---");
		condition.signal();
		System.out.println("---");
		proc.tick(System.currentTimeMillis());
		System.out.println("---");
	}
}
