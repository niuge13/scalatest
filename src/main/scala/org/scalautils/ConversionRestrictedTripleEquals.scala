/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalautils

/**
 * Provides <code>===</code> and <code>!==</code> operators that return <code>Boolean</code>, delegate the equality determination
 * to an <code>Equality</code> type class, and require that either the types of the two values compared are in a subtype/supertype
 * relationship, or that an implicit conversion is available that can convert from one type to the other.
 * 
 * <table><tr><td class="usage">
 * <strong>Recommended Usage</strong>:
 * Trait <code>ConversionRestrictedTripleEquals</code> is useful (in both production and test code) when you need determine equality for a type of object differently than
 * its <code>equals</code>
 * method&#8212;either you can't change the <code>equals</code> method, or the <code>equals</code> method is sensible generally, but you're in a special situation where you
 * need something else&#8212;and/or you want a compile-time type check that allows types that are implicitly convertable in either (or both) directions.
 * </td></tr></table>
 *
 * <p>
 * This trait is the middle ground of the three <em>triple equals</em> traits, in between
 * <code>TripleEquals</code>, the most lenient, and <code>TypeRestrictedTripleEquals</code>, the most strict.
 * If <code>TripleEquals</code> is mixed in or imported, the <code>===</code> can be used with any two types
 * and still compile. If <code>TypeRestrictedTripleEquals</code> is mixed in or imported, however, only types in 
 * a subtype or supertype relationship with each other (including when both types are exactly the same) will compile.
 * <code>ConversionRestrictedTripleEquals</code> is slightly more accomodating, because in addition to compiling any
 * use of <code>===</code> that will compile under </code>TypeRestrictedTripleEquals</code>, it will also compile
 * type types that would be rejected by <code>TypeRestrictedTripleEquals</code>, so long as an implicit
 * conversion (in either direction) from one type to another is available.
 * </p>
 *
 * <p>
 * For example, under <code>TypeRestrictedTripleEquals</code>, the following use of <code>===</code> will not compile,
 * because <code>Int</code> and <code>Long</code> are not in a subtype/supertype relationship. (<em>I.e.</em>, <code>Int</code>
 * is not a subtype or supertype of <code>Long</code>):
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; import org.scalautils._
 * import org.scalautils._
 * 
 * scala&gt; import TypeRestrictedTripleEquals._
 * import TypeRestrictedTripleEquals._
 * 
 * scala&gt; 1 === 1L
 * &lt;console&gt;:14: error: types Int and Long do not adhere to the equality constraint selected for
 * the === and !== operators; they must either be in a subtype/supertype relationship, or, if
 * ConversionRestrictedTripleEquals is in force, implicitly convertible in one direction or the other;
 * the missing implicit parameter is of type org.scalautils.TripleEqualsConstraint[Int,Long]
 *               1 === 1L
 *                 ^
 * </pre>
 *
 * <p>
 * Trait <code>TypeRestrictedTripleEquals</code> rejects types <code>Int</code> and <code>Long</code> because they are not directly related via
 * subtyping. However, an implicit widening conversion from <code>Int</code> to </code>Long</code> does exist (imported implicitly from
 * <code>scala.Predef</code>), so <code>ConversionRestrictedTripleEquals</code>
 * will allow it:
 * </p>
 * 
 * <pre class="stREPL">
 * scala&gt; import ConversionRestrictedTripleEquals._
 * import ConversionRestrictedTripleEquals._
 * 
 * scala&gt; 1 === 1L
 * res1: Boolean = true
 * </pre>
 * 
 * <p>
 * The implicit conversion can go in either direction: from the left type to the right type, or vice versa. In the above expression the 
 * implicit conversion goes from left to right (the <code>Int</code> on the left to the <code>Long</code> on the right). It also works
 * the other way:
 * </p>
 * 
 * <pre class="stREPL">
 * scala&gt; 1L === 1
 * res2: Boolean = true
 * </pre>
 * 
 * <p>
 * This trait will override or hide implicit methods defined by its sibling traits,
 * <a href="TripleEquals.html"><code>TripleEquals</code></a> or <a href="TypeRestrictedTripleEquals.html"><code>TypeRestrictedTripleEquals</code></a>,
 * and can therefore be used to temporarily turn on or off conversion checking in a limited scope. Here's an example, in which <code>TypeRestrictedTripleEquals</code> will
 * cause a compiler error:
 * </p>
 * 
 * <pre class="stHighlight">
 * import org.scalautils._
 * import TypeRestrictedTripleEquals._
 *
 * object Example {
 *
 *   def cmp(a: Int, b: Long): Int = {
 *     if (a === b) 0       // This line won't compile
 *     else if (a &lt; b) -1
 *     else 1
 *   }
 *
 *  def cmp(s: String, t: String): Int = {
 *    if (s === t) 0
 *    else if (s &lt; t) -1
 *    else 1
 *  }
 * }
 * </pre>
 *
 * Because <code>Int</code> and <code>Long</code> are not in a subtype/supertype relationship, comparing <code>1</code> and <code>1L</code> in the context
 * of <code>TypeRestrictedTripleEquals</code> will generate a compiler error:
 * </p>
 *
 * <pre>
 * Example.scala:9: error: types Int and Long do not adhere to the equality constraint selected for
 * the === and !== operators; they must either be in a subtype/supertype relationship, or, if
 * ConversionRestrictedTripleEquals is in force, implicitly convertible in one direction or the other;
 * the missing implicit parameter is of type org.scalautils.TripleEqualsConstraint[Int,Long]
 *     if (a === b) 0      // This line won't compile
 *           ^
 * one error found
 * </pre>
 * 
 * <p>
 * You can &ldquo;relax&rdquo; the type checking (<em>i.e.</em>, by additionally allowing implicitly convertible types) locally by importing
 * the members of <code>ConversionRestrictedTripleEquals</code> in a limited scope:
 * </p>
 * 
 * <pre class="stHighlight">
 * package org.scalautils.examples.conversioncheckedtripleequals
 * 
 * import org.scalautils._
 * import TypeRestrictedTripleEquals._
 * 
 * object Example {
 * 
 *   def cmp(a: Int, b: Long): Int = {
 *     import ConversionRestrictedTripleEquals._
 *     if (a === b) 0
 *     else if (a &lt; b) -1
 *     else 1
 *   }
 *
 *  def cmp(s: String, t: String): Int = {
 *    if (s === t) 0
 *    else if (s &lt; t) -1
 *    else 1
 *  }
 * }
 * </pre>
 *
 * <p>
 * With the above change, the <code>Example.scala</code> file compiles fine. Conversion checking is enabled only inside the first <code>cmp</code> method that
 * takes an <code>Int</code> and a <code>Long</code>. <code>TypeRestrictedTripleEquals</code> is still enforcing its type constraint, for example, for the <code>s === t</code>
 * expression in the other overloaded <code>cmp</code> method that takes strings.
 * </p>
 * 
 * <p>
 * Because the methods in <code>ConversionRestrictedTripleEquals</code> (and its siblings)
 * <em>override</em> all the methods defined in supertype <a href="TripleEqualsConstraints.html"><code>TripleEqualsConstraints</code></a>, you can achieve the same
 * kind of nested tuning of equality constraints whether you mix in traits, import from companion objects, or use some combination of both.
 * </p>
 *
 * <p>
 * In short, you should be able to select a primary constraint level via either a mixin or import, then change that in nested scopes
 * however you want, again either through a mixin or import, without getting any implicit conversion ambiguity. The innermost constraint level in scope
 * will always be in force.
 * <p>
 *
 * <p>
 * An alternative way to solve an unwanted compiler error caused by an over-zealous equality type constraint is to convert one side or the other to type
 * <code>Any</code>. Because <code>Any</code> is a supertype of everything, any level of equality type constraint will be satisfied. The <code>AsAny</code>
 * trait offers a convenient syntax, the <code>asAny</code> method, for this purpose:
 * </p>
 *
 * <pre class="stREPL">
 * scala&gt; import org.scalautils._
 * import org.scalautils._
 *
 * scala&gt; import ConversionRestrictedTripleEquals._
 * import ConversionRestrictedTripleEquals._
 *
 * scala&gt; List(1, 2, 3) === Vector(1, 2, 3)
&lt;console&gt;:14: error: types List[Int] and scala.collection.immutable.Vector[Int] do not adhere to the equality constraint selected for
 * the === and !== operators; they must either be in a subtype/supertype relationship, or, if
 * ConversionRestrictedTripleEquals is in force, implicitly convertible in one direction or the other;
 * the missing implicit parameter is of type org.scalautils.TripleEqualsConstraint[List[Int],scala.collection.immutable.Vector[Int]]
 *               List(1, 2, 3) === Vector(1, 2, 3)
 *                          ^
 *
 * scala&gt; import AsAny._
 * import AsAny._
 *
 * scala&gt; List(1, 2, 3) === Vector(1, 2, 3).asAny
 * res2: Boolean = true
 * </pre>
 * 
 * @author Bill Venners
 */
trait ConversionRestrictedTripleEquals extends LowPriorityConversionRestrictedConstraint {

  // Inherit the Scaladoc for these methods

  override def convertToEqualizer[T](left: T): Equalizer[T] = new Equalizer(left)
  implicit override def convertToCheckingEqualizer[T](left: T): CheckingEqualizer[T] = new CheckingEqualizer(left)

  override def convertToLegacyEqualizer[T](left: T): LegacyEqualizer[T] = new LegacyEqualizer(left)
  override def convertToLegacyCheckingEqualizer[T](left: T): LegacyCheckingEqualizer[T] = new LegacyCheckingEqualizer(left)

  override def unconstrainedEquality[A, B](implicit equalityOfA: Equality[A]): TripleEqualsConstraint[A, B] = new BasicEqualityConstraint[A, B](equalityOfA)

  override def lowPriorityTypeCheckedTripleEqualsConstraint[A, B](implicit equalityOfB: Equality[B], ev: A <:< B): TripleEqualsConstraint[A, B] = new AToBEqualityConstraint[A, B](equalityOfB, ev)
  override def typeCheckedTripleEqualsConstraint[A, B](implicit equalityOfA: Equality[A], ev: B <:< A): TripleEqualsConstraint[A, B] = new BToAEqualityConstraint[A, B](equalityOfA, ev)

  override def lowPriorityConversionCheckedTripleEqualsConstraint[A, B](implicit equalityOfB: Equality[B], cnv: A => B): TripleEqualsConstraint[A, B] = new AToBEqualityConstraint[A, B](equalityOfB, cnv)
  override def conversionCheckedTripleEqualsConstraint[A, B](implicit equalityOfA: Equality[A], cnv: B => A): TripleEqualsConstraint[A, B] = new BToAEqualityConstraint[A, B](equalityOfA, cnv)

  override def lowPriorityTypeRestrictedTripleEqualsConstraint[A, B](implicit equivalenceOfB: Equivalence[B], ev: A <:< B): TripleEqualsConstraint[A, B] = new AToBEquivalenceConstraint[A, B](equivalenceOfB, ev)
  override def typeRestrictedTripleEqualsConstraint[A, B](implicit equivalenceOfA: Equivalence[A], ev: B <:< A): TripleEqualsConstraint[A, B] = new BToAEquivalenceConstraint[A, B](equivalenceOfA, ev)
  implicit override def conversionRestrictedTripleEqualsConstraint[A, B](implicit equivalenceOfA: Equivalence[A], cnv: B => A): TripleEqualsConstraint[A, B] = new BToAEquivalenceConstraint[A, B](equivalenceOfA, cnv)
}

/**
 * Companion object to trait <code>ConversionRestrictedTripleEquals</code> that facilitates the importing of <code>ConversionRestrictedTripleEquals</code> members as 
 * an alternative to mixing it in. One use case is to import <code>ConversionRestrictedTripleEquals</code> members so you can use
 * them in the Scala interpreter:
 *
 * <pre class="stREPL">
 * $ scala -classpath scalautils.jar
 * Welcome to Scala version 2.10.0
 * Type in expressions to have them evaluated.
 * Type :help for more information.
 *
 * scala&gt; import org.scalautils._
 * import org.scalautils._
 * 
 * scala&gt; import ConversionRestrictedTripleEquals._
 * import ConversionRestrictedTripleEquals._
 * 
 * scala&gt; 1 === 1L
 * res0: Boolean = true
 * </pre>
 */
object ConversionRestrictedTripleEquals extends ConversionRestrictedTripleEquals

