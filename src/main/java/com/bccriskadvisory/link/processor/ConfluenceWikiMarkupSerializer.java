/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bccriskadvisory.link.processor;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.pegdown.Printer;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AnchorLinkNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;

import com.google.common.base.Joiner;

/**
 * A serializer for the pegdown markdown processor. Edgescan vulnerability details are written in markdown, but jira descriptions are written in
 * Confluence wiki markup. Note this serializer does not implement all features of markdown, as edgescan only uses a subset of markdown.
 */
public class ConfluenceWikiMarkupSerializer implements Visitor {
	
	private static final String BULLET = "*";
	private static final String NUMBER = "#";
	
	private Printer printer = new Printer();
	private List<String> errors = new ArrayList<>();
	private Deque<String> listPrefixStack = new ArrayDeque<>();
	private String listPrefix = "";

	public String toWikiMarkup(RootNode root) {
		root.accept(this);
		if (!errors.isEmpty()) {
			printer.print("Serialization errors:[" + Joiner.on(",").join(errors) + "]");
		}
		return printer.getString();
	}

	@Override
	public void visit(RootNode node) {
		visitChildren(node);
	}
	
	@Override
	public void visit(BlockQuoteNode node) {
		printer.print("{quote}").println();
		visitChildren(node);
		printer.print("{quote}").println();
	}

	@Override
	public void visit(CodeNode node) {
		printer.print("{code}").println();
		printer.print(node.getText());
		printer.print("{code}").println();
	}

	@Override
	public void visit(HtmlBlockNode node) {
        String text = node.getText();
        if (text.length() > 0) printer.println();
        printer.print(text);
	}
	
	@Override
	public void visit(BulletListNode node) {
		incrementListPrefix(BULLET);
		visitChildren(node);
		decrementListPrefix(BULLET);
	}
	
	@Override
	public void visit(OrderedListNode node) {
		incrementListPrefix(NUMBER);
		visitChildren(node);
		decrementListPrefix(NUMBER);
	}

	@Override
	public void visit(ListItemNode node) {
		if (listPrefixStack.isEmpty()) throw new IllegalStateException("No list indendation");
		
		printer.print(listPrefix);
		visitChildren(node);
		printer.println();
	}

	private void incrementListPrefix(String prefix) {
		listPrefixStack.push(prefix);
		listPrefix = Joiner.on("").join(listPrefixStack) + " ";
	}
	
	private void decrementListPrefix(String prefix) {
		if (listPrefixStack.isEmpty()) throw new IllegalStateException("Attempted to decrement list prefix but no indentation was found");
		
		if (!listPrefixStack.pop().equals(prefix)) throw new IllegalStateException("Attempted to decrement list prefix but incorrect indentation type was found");
		
		listPrefix = Joiner.on("").join(listPrefixStack);
	}

	@Override
	public void visit(ParaNode node) {
		printer.println();
		visitChildren(node);
		printer.println();
	}
	
	@Override
	public void visit(HeaderNode node) {
		printOneLiner(String.format("h%s. ", node.getLevel()), node);
	}

	@Override
	public void visit(QuotedNode node) {
		printOneLiner("bq. ", node);
	}

	private void printOneLiner(final String prefix, SuperNode node) {
		printer.print(prefix);
		visitChildren(node);
		printer.println();
	}

	@Override
	public void visit(StrikeNode node) {
		printInline(node, "-");
	}

	@Override
	public void visit(StrongEmphSuperNode node) {
    	if(node.isClosed()){
    		if(node.isStrong()) {
    			printInline(node, "*");
     		} else {
     			printInline(node, "_");
     		}
    	} else {
	    	//sequence was not closed, treat open chars as ordinary chars
	    	printer.print(node.getChars());
	    	visitChildren(node);
    	}
	}
	
	private void printInline(SuperNode node, String mark) {
		printer.print(mark);
		visitChildren(node);
		printer.print(mark);
	}
	
	@Override
	public void visit(InlineHtmlNode node) {
		printer.print(node.getText());
	}
	
	@Override
	public void visit(SpecialTextNode node) {
		printer.print(node.getText());
	}
	
	@Override
	public void visit(TextNode node) {
		printer.print(node.getText());
	}
	
	@Override
	public void visit(SuperNode node) {
		visitChildren(node);
	}
	
	private void visitChildren(SuperNode node) {
		for (Node child : node.getChildren()) {
			child.accept(this);
		}
	}
	
	private void logError(Node node, String... text) {
		errors.add(String.format("Unsupported node type: %s with contents %s", node.getClass(), Joiner.on(",").join(text)));
	}

	@Override
	public void visit(ReferenceNode node) {
		logError(node);
	}
	
	@Override
	public void visit(MailLinkNode node) {
		logError(node, node.getText());
	}
	
	@Override
	public void visit(AbbreviationNode node) {
		logError(node);
	}
	
	@Override
	public void visit(AnchorLinkNode node) {
		logError(node, node.getText());
	}
	
	@Override
	public void visit(AutoLinkNode node) {
		logError(node, node.getText());
	}
	
	@Override
	public void visit(DefinitionListNode node) {
		logError(node);
	}
	
	@Override
	public void visit(DefinitionNode node) {
		logError(node);
	}
	
	@Override
	public void visit(DefinitionTermNode node) {
		logError(node);
	}
	
	@Override
	public void visit(ExpImageNode node) {
		logError(node);
	}
	
	@Override
	public void visit(ExpLinkNode node) {
		logError(node);
	}

	@Override
	public void visit(RefImageNode node) {
		logError(node);
	}

	@Override
	public void visit(RefLinkNode node) {
		logError(node);
	}
	@Override
	public void visit(SimpleNode node) {

        switch (node.getType()) {
            case Apostrophe:
                printer.print("'");
                break;
            case Ellipsis:
                printer.print("...");
                break;
            case Emdash:
                printer.print("---");
                break;
            case Endash:
                printer.print("--");
                break;
            case HRule:
                printer.println().print("\\\\");
                break;
            case Linebreak:
                printer.print("\\\\");
                break;
            case Nbsp:
                printer.print(" ");
                break;
            default:
                throw new IllegalStateException();
        }
	}

	@Override
	public void visit(TableBodyNode node) {
		logError(node);
	}

	@Override
	public void visit(TableCaptionNode node) {
		logError(node);
	}

	@Override
	public void visit(TableCellNode node) {
		logError(node);
	}

	@Override
	public void visit(TableColumnNode node) {
		logError(node);
	}

	@Override
	public void visit(TableHeaderNode node) {
		logError(node);
	}

	@Override
	public void visit(TableNode node) {
		logError(node);
	}

	@Override
	public void visit(TableRowNode node) {
		logError(node);
	}

	@Override
	public void visit(VerbatimNode node) {
		logError(node);
	}

	@Override
	public void visit(WikiLinkNode node) {
		logError(node);
	}

	@Override
	public void visit(Node node) {
		logError(node);
	}
}
