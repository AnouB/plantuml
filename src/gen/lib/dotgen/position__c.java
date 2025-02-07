/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of Smetana.
 * Smetana is a partial translation of Graphviz/Dot sources from C to Java.
 *
 * (C) Copyright 2009-2022, Arnaud Roques
 *
 * This translation is distributed under the same Licence as the original C program:
 * 
 *************************************************************************
 * Copyright (c) 2011 AT&T Intellectual Property 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: See CVS logs. Details at http://www.graphviz.org/
 *************************************************************************
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC
 * LICENSE ("AGREEMENT"). [Eclipse Public License - v 1.0]
 * 
 * ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM CONSTITUTES
 * RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 * 
 * You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package gen.lib.dotgen;
import static gen.lib.cgraph.attr__c.agget;
import static gen.lib.cgraph.edge__c.aghead;
import static gen.lib.cgraph.edge__c.agtail;
import static gen.lib.cgraph.graph__c.agnnodes;
import static gen.lib.cgraph.obj__c.agcontains;
import static gen.lib.cgraph.obj__c.agroot;
import static gen.lib.common.ns__c.rank;
import static gen.lib.common.splines__c.selfRightSpace;
import static gen.lib.common.utils__c.late_int;
import static gen.lib.dotgen.cluster__c.mark_lowclusters;
import static gen.lib.dotgen.conc__c.dot_concentrate;
import static gen.lib.dotgen.dotinit__c.dot_root;
import static gen.lib.dotgen.fastgr__c.fast_edge;
import static gen.lib.dotgen.fastgr__c.find_fast_edge;
import static gen.lib.dotgen.fastgr__c.virtual_node;
import static gen.lib.dotgen.fastgr__c.zapinlist;
import static gen.lib.dotgen.flat__c.flat_edges;
import static smetana.core.JUtils.USHRT_MAX;
import static smetana.core.JUtils.atof;
import static smetana.core.Macro.AGINEDGE;
import static smetana.core.Macro.AGOUTEDGE;
import static smetana.core.Macro.AGTYPE;
import static smetana.core.Macro.BOTTOM_IX;
import static smetana.core.Macro.CL_OFFSET;
import static smetana.core.Macro.ED_dist;
import static smetana.core.Macro.ED_head_port;
import static smetana.core.Macro.ED_label;
import static smetana.core.Macro.ED_minlen;
import static smetana.core.Macro.ED_tail_port;
import static smetana.core.Macro.ED_to_orig;
import static smetana.core.Macro.ED_weight;
import static smetana.core.Macro.GD_bb;
import static smetana.core.Macro.GD_border;
import static smetana.core.Macro.GD_clust;
import static smetana.core.Macro.GD_drawing;
import static smetana.core.Macro.GD_exact_ranksep;
import static smetana.core.Macro.GD_flip;
import static smetana.core.Macro.GD_has_labels;
import static smetana.core.Macro.GD_ht1;
import static smetana.core.Macro.GD_ht2;
import static smetana.core.Macro.GD_label;
import static smetana.core.Macro.GD_ln;
import static smetana.core.Macro.GD_maxrank;
import static smetana.core.Macro.GD_minrank;
import static smetana.core.Macro.GD_n_cluster;
import static smetana.core.Macro.GD_nlist;
import static smetana.core.Macro.GD_nodesep;
import static smetana.core.Macro.GD_rank;
import static smetana.core.Macro.GD_ranksep;
import static smetana.core.Macro.GD_rn;
import static smetana.core.Macro.INT_MAX;
import static smetana.core.Macro.LEAFSET;
import static smetana.core.Macro.M_aghead;
import static smetana.core.Macro.M_agtail;
import static smetana.core.Macro.ND_UF_size;
import static smetana.core.Macro.ND_alg;
import static smetana.core.Macro.ND_clust;
import static smetana.core.Macro.ND_coord;
import static smetana.core.Macro.ND_flat_out;
import static smetana.core.Macro.ND_ht;
import static smetana.core.Macro.ND_in;
import static smetana.core.Macro.ND_inleaf;
import static smetana.core.Macro.ND_lw;
import static smetana.core.Macro.ND_mval;
import static smetana.core.Macro.ND_next;
import static smetana.core.Macro.ND_node_type;
import static smetana.core.Macro.ND_order;
import static smetana.core.Macro.ND_other;
import static smetana.core.Macro.ND_out;
import static smetana.core.Macro.ND_outleaf;
import static smetana.core.Macro.ND_prev;
import static smetana.core.Macro.ND_rank;
import static smetana.core.Macro.ND_ranktype;
import static smetana.core.Macro.ND_rw;
import static smetana.core.Macro.ND_save_in;
import static smetana.core.Macro.ND_save_out;
import static smetana.core.Macro.ROUND;
import static smetana.core.Macro.TOP_IX;
import static smetana.core.Macro.UNSUPPORTED;
import static smetana.core.Macro.UNSURE_ABOUT;
import static smetana.core.Macro.alloc_elist;
import static smetana.core.Macro.free_list;
import static smetana.core.debug.SmetanaDebug.ENTERING;
import static smetana.core.debug.SmetanaDebug.LEAVING;

import gen.annotation.Difficult;
import gen.annotation.HasND_Rank;
import gen.annotation.Original;
import gen.annotation.Reviewed;
import gen.annotation.Todo;
import gen.annotation.Unused;
import h.EN_ratio_t;
import h.ST_Agedge_s;
import h.ST_Agedgeinfo_t;
import h.ST_Agedgepair_s;
import h.ST_Agnode_s;
import h.ST_Agraph_s;
import h.ST_aspect_t;
import h.ST_point;
import h.ST_pointf;
import h.ST_rank_t;
import smetana.core.CArray;
import smetana.core.CArrayOfStar;
import smetana.core.CString;
import smetana.core.Globals;
import smetana.core.Memory;
import smetana.core.ZType;


/*
 * position(g): set ND_coord(n) (x and y) for all nodes n of g, using GD_rank(g).
 * (the graph may be modified by merging certain edges with a common endpoint.)
 * the coordinates are computed by constructing and ranking an auxiliary graph.
 * then leaf nodes are inserted in the fast graph.  cluster boundary nodes are
 * created and correctly separated.
 */
public class position__c {



//3 6knxbdrmgk6294aw61s2lpvvf
// static double largeMinlen (double l) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="largeMinlen", key="6knxbdrmgk6294aw61s2lpvvf", definition="static double largeMinlen (double l)")
public static double largeMinlen(double l) {
ENTERING("6knxbdrmgk6294aw61s2lpvvf","largeMinlen");
try {
 UNSUPPORTED("lt6cippjix5bbvyhkcpl8g7g"); // static double
UNSUPPORTED("e2f0xhw6om2fpgt48xyjjg3i"); // largeMinlen (double l)
UNSUPPORTED("erg9i1970wdri39osu8hx2a6e"); // {
UNSUPPORTED("dad2o3vzemegi5fywxl7hcezk"); //     agerr (AGERR, "Edge length %f larger than maximum %u allowed.\nCheck for overwide node(s).\n", l, USHRT_MAX); 
UNSUPPORTED("dlasv24dnuygpwagcamhyg15w"); //     return (double)USHRT_MAX;
UNSUPPORTED("c24nfmv9i7o5eoqaymbibp7m7"); // }

throw new UnsupportedOperationException();
} finally {
LEAVING("6knxbdrmgk6294aw61s2lpvvf","largeMinlen");
}
}




//3 ccowbxkwmrj75tojopmhcmubx
// static void connectGraph (graph_t* g) 
@Unused
@HasND_Rank
@Original(version="2.38.0", path="lib/dotgen/position.c", name="connectGraph", key="ccowbxkwmrj75tojopmhcmubx", definition="static void connectGraph (graph_t* g)")
public static void connectGraph(ST_Agraph_s g) {
ENTERING("ccowbxkwmrj75tojopmhcmubx","connectGraph");
try {
    int i, j, r, found;
    ST_Agnode_s tp;
    ST_Agnode_s hp;
    ST_Agnode_s sn;
    ST_Agedge_s e;
    CArray<ST_rank_t> rp;
    
    for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	rp = GD_rank(g).plus_(r);
	found =0;
        tp = null;
	for (i = 0; i < rp.get__(0).n; i++) {
	    tp = (ST_Agnode_s) rp.get__(0).v.get_(i);
	    if (ND_save_out(tp).list!=null) {
        	for (j = 0; (e = (ST_Agedge_s) ND_save_out(tp).list.get_(j))!=null; j++) {
		    if ((ND_rank(aghead(e)) > r) || (ND_rank(agtail(e)) > r)) {
			found = 1;
			break;
		    }
        	}
		if (found!=0) break;
	    }
	    if (ND_save_in(tp).list!=null) {
        	for (j = 0; (e = (ST_Agedge_s) ND_save_in(tp).list.get_(j))!=null; j++) {
		    if ((ND_rank(agtail(e)) > r) || (ND_rank(aghead(e)) > r)) {
			found = 1;
			break;
		    }
        	}
		if (found!=0) break;
	    }
	}
	if (found!=0 || (tp) == null) continue;
	tp = rp.get__(0).v.get_(0);
	if (r < GD_maxrank(g)) hp = (ST_Agnode_s) rp.get__(1).v.get_(0);
	else hp = (ST_Agnode_s) rp.get__(-1).v.get_(0);
	//assert (hp);
	sn = virtual_node(g);
	ND_node_type(sn, 2);
	make_aux_edge(sn, tp, 0, 0);
	make_aux_edge(sn, hp, 0, 0);
	ND_rank(sn, Math.min(ND_rank(tp), ND_rank(hp)));
    }
} finally {
LEAVING("ccowbxkwmrj75tojopmhcmubx","connectGraph");
}
}




@Reviewed(when = "15/11/2020")
@Original(version="2.38.0", path="lib/dotgen/position.c", name="dot_position", key="33snzyd9z0loienur06dnily9", definition="void dot_position(graph_t * g, aspect_t* asp)")
public static void dot_position(Globals zz, ST_Agraph_s g, ST_aspect_t asp) {
ENTERING("33snzyd9z0loienur06dnily9","dot_position");
try {
    if (GD_nlist(g) == null)
	return;			/* ignore empty graph */
    mark_lowclusters(zz, g);	/* we could remove from splines.c now */
    set_ycoords(zz, g);
    if (zz.Concentrate)
	dot_concentrate(g);
    expand_leaves(g);
    if (flat_edges(zz, g))
	set_ycoords(zz, g);
    create_aux_edges(zz, g);
    if (rank(zz, g, 2, nsiter2(zz, g))!=0) { /* LR balance == 2 */
	connectGraph (g);
	//assert(rank(g, 2, nsiter2(g)) == 0);
    }
    set_xcoords(g);
    set_aspect(g, asp);
    remove_aux_edges(g);	/* must come after set_aspect since we now
				 * use GD_ln and GD_rn for bbox width.
				 */
} finally {
LEAVING("33snzyd9z0loienur06dnily9","dot_position");
}
}




//3 90vn63m6v0w9fn9a2dgfxxx3h
// static int nsiter2(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="nsiter2", key="90vn63m6v0w9fn9a2dgfxxx3h", definition="static int nsiter2(graph_t * g)")
public static int nsiter2(Globals zz, ST_Agraph_s g) {
ENTERING("90vn63m6v0w9fn9a2dgfxxx3h","nsiter2");
try {
    int maxiter = INT_MAX;
    CString s;
    if ((s = agget(zz, g, new CString("nslimit")))!=null)
	maxiter = (int)(atof(s) * agnnodes(g));
    return maxiter;
} finally {
LEAVING("90vn63m6v0w9fn9a2dgfxxx3h","nsiter2");
}
}




//3 5bax8ut6nnk4pr7yxdumk9chl
// static int go(node_t * u, node_t * v) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="go", key="5bax8ut6nnk4pr7yxdumk9chl", definition="static int go(node_t * u, node_t * v)")
public static boolean go(ST_Agnode_s u, ST_Agnode_s v) {
ENTERING("5bax8ut6nnk4pr7yxdumk9chl","go");
try {
    int i;
    ST_Agedge_s e;
    if (u == v)
	return true;
    for (i = 0; (e = (ST_Agedge_s) ND_out(u).list.get_(i))!=null; i++) {
	if (go(aghead(e), v))
	    return true;
    }
    return false;
} finally {
LEAVING("5bax8ut6nnk4pr7yxdumk9chl","go");
}
}




//3 9xz8numztzj4qsq85pziahv1k
// static int canreach(node_t * u, node_t * v) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="canreach", key="9xz8numztzj4qsq85pziahv1k", definition="static int canreach(node_t * u, node_t * v)")
public static boolean canreach(ST_Agnode_s u, ST_Agnode_s v) {
ENTERING("9xz8numztzj4qsq85pziahv1k","canreach");
try {
    return go(u, v);
} finally {
LEAVING("9xz8numztzj4qsq85pziahv1k","canreach");
}
}




//3 4cvgiatny97ou6mhqoq6aqwek
// edge_t *make_aux_edge(node_t * u, node_t * v, double len, int wt) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="", key="4cvgiatny97ou6mhqoq6aqwek", definition="edge_t *make_aux_edge(node_t * u, node_t * v, double len, int wt)")
public static ST_Agedge_s make_aux_edge(ST_Agnode_s u, ST_Agnode_s v, double len, int wt) {
ENTERING("4cvgiatny97ou6mhqoq6aqwek","make_aux_edge");
try {
    ST_Agedge_s e;
    ST_Agedgepair_s e2 = new ST_Agedgepair_s();
    AGTYPE(e2.in, AGINEDGE);
    AGTYPE(e2.out, AGOUTEDGE);
    e2.out.base.data = new ST_Agedgeinfo_t();
    e = (ST_Agedge_s) e2.out;
    M_agtail(e, u);
    M_aghead(e, v);
    if (len > USHRT_MAX)
	len = largeMinlen (len);
    ED_minlen(e, ROUND(len));
    ED_weight(e, wt);
    fast_edge(e);
    return e;
} finally {
LEAVING("4cvgiatny97ou6mhqoq6aqwek","make_aux_edge");
}
}




//3 53fvij7oun7aezlb7x66vzuyb
// static void allocate_aux_edges(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="allocate_aux_edges", key="53fvij7oun7aezlb7x66vzuyb", definition="static void allocate_aux_edges(graph_t * g)")
public static void allocate_aux_edges(ST_Agraph_s g) {
ENTERING("53fvij7oun7aezlb7x66vzuyb","allocate_aux_edges");
try {
    int i, j, n_in;
    ST_Agnode_s n;
    /* allocate space for aux edge lists */
    for (n = GD_nlist(g); n!=null; n = ND_next(n)) {
	ND_save_in(n, ND_in(n));
	ND_save_out(n, ND_out(n));
	for (i = 0; ND_out(n).list.get_(i)!=null; i++);
	for (j = 0; ND_in(n).list.get_(j)!=null; j++);
	n_in = i + j;
	alloc_elist(n_in + 3, ND_in(n));
	alloc_elist(3, ND_out(n));
    }
} finally {
LEAVING("53fvij7oun7aezlb7x66vzuyb","allocate_aux_edges");
}
}




//3 ah28nr6mxpjeosr85bhmzd3si
// static void  make_LR_constraints(graph_t * g) 
@Unused
@HasND_Rank
@Original(version="2.38.0", path="lib/dotgen/position.c", name="make_LR_constraints", key="ah28nr6mxpjeosr85bhmzd3si", definition="static void  make_LR_constraints(graph_t * g)")
public static void make_LR_constraints(ST_Agraph_s g) {
ENTERING("ah28nr6mxpjeosr85bhmzd3si","make_LR_constraints");
try {
    int i, j, k;
    int sw;			/* self width */
    int m0, m1;
    double width;
    int sep[] = new int[2];
    int nodesep;      /* separation between nodes on same rank */
    ST_Agedge_s e, e0, e1, ff;
    ST_Agnode_s u, v, t0, h0;
    CArray<ST_rank_t> rank = GD_rank(g);
    /* Use smaller separation on odd ranks if g has edge labels */
    if ((GD_has_labels(g) & (1 << 0))!=0) {
	sep[0] = GD_nodesep(g);
	sep[1] = 5;
    }
    else {
	sep[1] = sep[0] = GD_nodesep(g);
    }
    /* make edges to constrain left-to-right ordering */
    for (i = GD_minrank(g); i <= GD_maxrank(g); i++) {
	double last;
	ND_rank(rank.get__(i).v.get_(0), 0);
	last = 0;
	nodesep = sep[i & 1];
	for (j = 0; j < rank.get__(i).n; j++) {
	    u = rank.get__(i).v.get_(j);
	    ND_mval(u, ND_rw(u));	/* keep it somewhere safe */
	    if (ND_other(u).size > 0) {	/* compute self size */
		/* FIX: dot assumes all self-edges go to the right. This
                 * is no longer true, though makeSelfEdge still attempts to
                 * put as many as reasonable on the right. The dot code
                 * should be modified to allow a box reflecting the placement
                 * of all self-edges, and use that to reposition the nodes.
                 * Note that this would not only affect left and right
                 * positioning but may also affect interrank spacing.
                 */
		sw = 0;
		for (k = 0; (e = (ST_Agedge_s) ND_other(u).list.get_(k))!=null; k++) {
		    if (agtail(e) == aghead(e)) {
			sw += selfRightSpace (e);
		    }
		}
		ND_rw(u, ND_rw(u) + sw);	/* increment to include self edges */
	    }
	    v = rank.get__(i).v.get_(j + 1);
	    if (v!=null) {
		width = ND_rw(u) + ND_lw(v) + nodesep;
		e0 = make_aux_edge(u, v, width, 0);
		ND_rank(v, (int)(last + width));
		last = (int)(last + width);
	    }
	    /* constraints from labels of flat edges on previous rank */
	    if ((e = (ST_Agedge_s) ND_alg(u))!=null) {
		e0 = (ST_Agedge_s) ND_save_out(u).list.get_(0);
		e1 = (ST_Agedge_s) ND_save_out(u).list.get_(1);
		if (ND_order(aghead(e0)) > ND_order(aghead(e1))) {
		    ff = e0;
		    e0 = e1;
		    e1 = ff;
		}
		m0 = (ED_minlen(e) * GD_nodesep(g)) / 2;
		m1 = m0 + ((int)(ND_rw(aghead(e0)) + ND_lw(agtail(e0))));
		/* these guards are needed because the flat edges
		 * work very poorly with cluster layout */
		if (canreach(agtail(e0), aghead(e0)) == false)
		    make_aux_edge(aghead(e0), agtail(e0), m1,
			ED_weight(e));
		m1 = m0 + ((int)(ND_rw(agtail(e1)) + ND_lw(aghead(e1))));
		if (canreach(aghead(e1), agtail(e1)) == false)
		    make_aux_edge(agtail(e1), aghead(e1), m1,
			ED_weight(e));
	    }
	    /* position flat edge endpoints */
	    for (k = 0; k < ND_flat_out(u).size; k++) {
		e = (ST_Agedge_s) ND_flat_out(u).list.get_(k);
		if (ND_order(agtail(e)) < ND_order(aghead(e))) {
		    t0 = agtail(e);
		    h0 = aghead(e);
		} else {
		    t0 = aghead(e);
		    h0 = agtail(e);
		}
		width = ND_rw(t0) + ND_lw(h0);
		m0 = (int) (ED_minlen(e) * GD_nodesep(g) + width);
		if ((e0 = find_fast_edge(t0, h0))!=null) {
		    /* flat edge between adjacent neighbors 
                     * ED_dist contains the largest label width.
                     */
		    m0 = Math.max(m0, (int)(width + GD_nodesep(g) + ROUND(ED_dist(e))));
		    if (m0 > USHRT_MAX)
			m0 = (int) largeMinlen (m0);
		    ED_minlen(e0, Math.max(ED_minlen(e0), m0));
		    ED_weight(e0, Math.max(ED_weight(e0), ED_weight(e)));
		}
		else if ((ED_label(e)) == null) {
		    /* unlabeled flat edge between non-neighbors 
		     * ED_minlen(e) is max of ED_minlen of all equivalent 
                     * edges.
                     */
		    make_aux_edge(t0, h0, m0, ED_weight(e));
		}
		/* labeled flat edges between non-neighbors have already
                 * been constrained by the label above. 
                 */ 
	    }
	}
    }
} finally {
LEAVING("ah28nr6mxpjeosr85bhmzd3si","make_LR_constraints");
}
}




//3 6uruo8mutxgcni9fm8jcrw4cr
// static void make_edge_pairs(graph_t * g) 
@Unused
@HasND_Rank
@Original(version="2.38.0", path="lib/dotgen/position.c", name="make_edge_pairs", key="6uruo8mutxgcni9fm8jcrw4cr", definition="static void make_edge_pairs(graph_t * g)")
public static void make_edge_pairs(ST_Agraph_s g) {
ENTERING("6uruo8mutxgcni9fm8jcrw4cr","make_edge_pairs");
try {
    int i, m0, m1;
    ST_Agnode_s n, sn;
    ST_Agedge_s e;
    for (n = GD_nlist(g); n!=null; n = ND_next(n)) {
	if (ND_save_out(n).list!=null)
	    for (i = 0; (e = (ST_Agedge_s) ND_save_out(n).list.get_(i))!=null; i++) {
		sn = virtual_node(g);
		ND_node_type(sn, 2);
		m0 = (int)(ED_head_port(e).p.x - ED_tail_port(e).p.x);
		if (m0 > 0)
		    m1 = 0;
		else {
		    m1 = -m0;
		    m0 = 0;
		}
		make_aux_edge(sn, agtail(e), m0 + 1, ED_weight(e));
		make_aux_edge(sn, aghead(e), m1 + 1, ED_weight(e));
		ND_rank(sn,
		    Math.min(ND_rank(agtail(e)) - m0 - 1,
			ND_rank(aghead(e)) - m1 - 1));
	    }
    }
} finally {
LEAVING("6uruo8mutxgcni9fm8jcrw4cr","make_edge_pairs");
}
}




//3 79v3omwzni0nm3h05l3onjsbz
// static void contain_clustnodes(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="contain_clustnodes", key="79v3omwzni0nm3h05l3onjsbz", definition="static void contain_clustnodes(graph_t * g)")
public static void contain_clustnodes(Globals zz, ST_Agraph_s g) {
ENTERING("79v3omwzni0nm3h05l3onjsbz","contain_clustnodes");
try {
    int c;
    ST_Agedge_s e;
    if ((g != dot_root(g))) {
	contain_nodes(zz, g);
	if ((e = find_fast_edge(GD_ln(g),GD_rn(g)))!=null)	/* maybe from lrvn()?*/
	    ED_weight(e, ED_weight(e) + 128);
	else
	    make_aux_edge(GD_ln(g), GD_rn(g), 1, 128);	/* clust compaction edge */
    }
    for (c = 1; c <= GD_n_cluster(g); c++)
	contain_clustnodes(zz, (ST_Agraph_s) GD_clust(g).get_(c));
} finally {
LEAVING("79v3omwzni0nm3h05l3onjsbz","contain_clustnodes");
}
}




//3 24yfgklubun581fbfyx62lzsm
// static int vnode_not_related_to(graph_t * g, node_t * v) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="vnode_not_related_to", key="24yfgklubun581fbfyx62lzsm", definition="static int vnode_not_related_to(graph_t * g, node_t * v)")
public static boolean vnode_not_related_to(Globals zz, ST_Agraph_s g, ST_Agnode_s v) {
ENTERING("24yfgklubun581fbfyx62lzsm","vnode_not_related_to");
try {
    ST_Agedge_s e;
    if (ND_node_type(v) != 1)
	return false;
    for (e = (ST_Agedge_s) ND_save_out(v).list.get_(0); ED_to_orig(e)!=null; e = ED_to_orig(e));
    if (agcontains(zz, g, agtail(e)))
	return false;
    if (agcontains(zz, g, aghead(e)))
	return false;
    return true;
} finally {
LEAVING("24yfgklubun581fbfyx62lzsm","vnode_not_related_to");
}
}




//3 73cdgjl47ohty2va766evbo4
// static void keepout_othernodes(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="keepout_othernodes", key="73cdgjl47ohty2va766evbo4", definition="static void keepout_othernodes(graph_t * g)")
public static void keepout_othernodes(Globals zz, ST_Agraph_s g) {
ENTERING("73cdgjl47ohty2va766evbo4","keepout_othernodes");
try {
    int i, c, r, margin;
    ST_Agnode_s u, v;
    margin = late_int (g, zz.G_margin, 8, 0);
    for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	if (GD_rank(g).get__(r).n == 0)
	    continue;
	v = (ST_Agnode_s) GD_rank(g).get__(r).v.get_(0);
	if (v == null)
	    continue;
	for (i = ND_order(v) - 1; i >= 0; i--) {
	    u = (ST_Agnode_s) GD_rank(dot_root(g)).get__(r).v.get_(i);
	    /* can't use "is_a_vnode_of" because elists are swapped */
	    if ((ND_node_type(u) == 0) || vnode_not_related_to(zz, g, u)) {
		make_aux_edge(u, GD_ln(g), margin + ND_rw(u), 0);
		break;
	    }
	}
	for (i = ND_order(v) + GD_rank(g).get__(r).n; i < GD_rank(dot_root(g)).get__(r).n;
	     i++) {
	    u = (ST_Agnode_s) GD_rank(dot_root(g)).get__(r).v.get_(i);
	    if ((ND_node_type(u) == 0) || vnode_not_related_to(zz, g, u)) {
		make_aux_edge(GD_rn(g), u, margin + ND_lw(u), 0);
		break;
	    }
	}
    }
    for (c = 1; c <= GD_n_cluster(g); c++)
	keepout_othernodes(zz, (ST_Agraph_s) GD_clust(g).get_(c));
} finally {
LEAVING("73cdgjl47ohty2va766evbo4","keepout_othernodes");
}
}




//3 c734mx1638sfqtl7vh7itaxyx
// static void contain_subclust(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="contain_subclust", key="c734mx1638sfqtl7vh7itaxyx", definition="static void contain_subclust(graph_t * g)")
public static void contain_subclust(Globals zz, ST_Agraph_s g) {
ENTERING("c734mx1638sfqtl7vh7itaxyx","contain_subclust");
try {
    int margin, c;
    ST_Agraph_s subg;
    margin = late_int (g, zz.G_margin, 8, 0);
    make_lrvn(g);
    for (c = 1; c <= GD_n_cluster(g); c++) {
	subg = GD_clust(g).get_(c);
	make_lrvn(subg);
	make_aux_edge(GD_ln(g), GD_ln(subg),
		      margin + GD_border(g)[3].x, 0);
	make_aux_edge(GD_rn(subg), GD_rn(g),
		      margin + GD_border(g)[1].x, 0);
	contain_subclust(zz, subg);
    }
} finally {
LEAVING("c734mx1638sfqtl7vh7itaxyx","contain_subclust");
}
}




//3 6oruu1p1b7kxr5moh3kmcmvr3
// static void separate_subclust(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="separate_subclust", key="6oruu1p1b7kxr5moh3kmcmvr3", definition="static void separate_subclust(graph_t * g)")
public static void separate_subclust(Globals zz, ST_Agraph_s g) {
ENTERING("6oruu1p1b7kxr5moh3kmcmvr3","separate_subclust");
try {
    int i, j, margin;
    ST_Agraph_s low, high;
    ST_Agraph_s left, right;
    margin = late_int (g, zz.G_margin, 8, 0);
    for (i = 1; i <= GD_n_cluster(g); i++)
	make_lrvn(GD_clust(g).get_(i));
    for (i = 1; i <= GD_n_cluster(g); i++) {
	for (j = i + 1; j <= GD_n_cluster(g); j++) {
	    low = GD_clust(g).get_(i);
	    high = GD_clust(g).get_(j);
	    if (GD_minrank(low) > GD_minrank(high)) {
		ST_Agraph_s temp = low;
		low = high;
		high = temp;
	    }
	    if (GD_maxrank(low) < GD_minrank(high))
		continue;
	    if (ND_order(GD_rank(low).get__(GD_minrank(high)).v.get_(0))
		< ND_order(GD_rank(high).get__(GD_minrank(high)).v.get_(0))) {
		left = low;
		right = high;
	    } else {
		left = high;
		right = low;
	    }
	    make_aux_edge(GD_rn(left), GD_ln(right), margin, 0);
	}
	separate_subclust(zz, GD_clust(g).get_(i));
    }
} finally {
LEAVING("6oruu1p1b7kxr5moh3kmcmvr3","separate_subclust");
}
}




//3 8f8gs2zivo4pnd3hmtb9g23x4
// static void pos_clusters(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="pos_clusters", key="8f8gs2zivo4pnd3hmtb9g23x4", definition="static void pos_clusters(graph_t * g)")
public static void pos_clusters(Globals zz, ST_Agraph_s g) {
ENTERING("8f8gs2zivo4pnd3hmtb9g23x4","pos_clusters");
try {
    if (GD_n_cluster(g) > 0) {
	contain_clustnodes(zz, g);
	keepout_othernodes(zz, g);
	contain_subclust(zz, g);
	separate_subclust(zz, g);
    }
} finally {
LEAVING("8f8gs2zivo4pnd3hmtb9g23x4","pos_clusters");
}
}




//3 fywsxto7yvl5wa2dfu7u7jj1
// static void compress_graph(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="compress_graph", key="fywsxto7yvl5wa2dfu7u7jj1", definition="static void compress_graph(graph_t * g)")
public static void compress_graph(ST_Agraph_s g) {
ENTERING("fywsxto7yvl5wa2dfu7u7jj1","compress_graph");
try {
    double x;
    ST_pointf p = new ST_pointf();
    if (GD_drawing(g).ratio_kind != EN_ratio_t.R_COMPRESS)
	return;
UNSUPPORTED("79oeaf0u32si2chjcpas5whjl"); //     p = GD_drawing(g)->size;
UNSUPPORTED("6a2ue1i6kvwvpgapb4z8l27jn"); //     if (p.x * p.y <= 1)
UNSUPPORTED("a7fgam0j0jm7bar0mblsv3no4"); // 	return;
UNSUPPORTED("5f3k9yz6btwxc8r5t8exytqqt"); //     contain_nodes(g);
UNSUPPORTED("4mvbrmj6dfhaz3burnpac7zsx"); //     if (GD_flip(g) == 0)
UNSUPPORTED("dzkztznjq2andjnjzqh8i5tij"); // 	x = p.x;
UNSUPPORTED("div10atae09n36x269sl208r1"); //     else
UNSUPPORTED("ddzjdkqij3y4gq9i3hikpoqvj"); // 	x = p.y;
UNSUPPORTED("e1xij2jh66kyaiikenemo1qza"); //     /* Guard against huge size attribute since max. edge length is USHRT_MAX
UNSUPPORTED("5ilmnsqirjhzn5q6s3f9pkgi3"); //      * A warning might be called for. Also, one could check that the graph
UNSUPPORTED("1nhxkt9jijvhw5gsp2pluh8g8"); //      * already fits GD_drawing(g)->size and return immediately.
UNSUPPORTED("795vpnc8yojryr8b46aidsu69"); //      */
UNSUPPORTED("dkqac1chvtsaao23vr43xqs5r"); //     x = MIN(x,USHRT_MAX); 
UNSUPPORTED("5es2j3xrdatvha5uea2wlqcxp"); //     make_aux_edge(GD_ln(g), GD_rn(g), x, 1000);
UNSUPPORTED("c24nfmv9i7o5eoqaymbibp7m7"); // }

throw new UnsupportedOperationException();
} finally {
LEAVING("fywsxto7yvl5wa2dfu7u7jj1","compress_graph");
}
}




//3 b7y0htx4svbhaqb1a12dihlue
// static void create_aux_edges(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="create_aux_edges", key="b7y0htx4svbhaqb1a12dihlue", definition="static void create_aux_edges(graph_t * g)")
public static void create_aux_edges(Globals zz, ST_Agraph_s g) {
ENTERING("b7y0htx4svbhaqb1a12dihlue","create_aux_edges");
try {
    allocate_aux_edges(g);
    make_LR_constraints(g);
    make_edge_pairs(g);
    pos_clusters(zz, g);
    compress_graph(g);
} finally {
LEAVING("b7y0htx4svbhaqb1a12dihlue","create_aux_edges");
}
}




//3 euzeilq92ry8a4tcrij5s52t5
// static void remove_aux_edges(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="remove_aux_edges", key="euzeilq92ry8a4tcrij5s52t5", definition="static void remove_aux_edges(graph_t * g)")
public static void remove_aux_edges(ST_Agraph_s g) {
ENTERING("euzeilq92ry8a4tcrij5s52t5","remove_aux_edges");
try {
    int i;
    ST_Agnode_s n, nnext, nprev;
    ST_Agedge_s e;
    for (n = GD_nlist(g); n!=null; n = ND_next(n)) {
	for (i = 0; (e = (ST_Agedge_s) ND_out(n).list.get_(i))!=null; i++) {
	    Memory.free(e.base.data);
	    Memory.free(e);
	}
	free_list(ND_out(n));
	free_list(ND_in(n));
	ND_out(n, ND_save_out(n));
	ND_in(n, ND_save_in(n));
    }
    /* cannot be merged with previous loop */
    nprev = null;
    for (n = GD_nlist(g); n!=null; n = nnext) {
	nnext = ND_next(n);
	if (ND_node_type(n) == 2) {
	    if (nprev!=null)
		ND_next(nprev, nnext);
	    else
		GD_nlist(g, nnext);
	    Memory.free(n.base.data);
	    Memory.free(n);
	} else
	    nprev = n;
    }
    ND_prev(GD_nlist(g), null);
} finally {
LEAVING("euzeilq92ry8a4tcrij5s52t5","remove_aux_edges");
}
}




//3 1oobmglea9t819y95xeel37h8
// static void  set_xcoords(graph_t * g) 
@Unused
@HasND_Rank
@Original(version="2.38.0", path="lib/dotgen/position.c", name="set_xcoords", key="1oobmglea9t819y95xeel37h8", definition="static void  set_xcoords(graph_t * g)")
public static void set_xcoords(ST_Agraph_s g) {
ENTERING("1oobmglea9t819y95xeel37h8","set_xcoords");
try {
    int i, j;
    ST_Agnode_s v;
    CArray<ST_rank_t> rank = GD_rank(g);
    for (i = GD_minrank(g); i <= GD_maxrank(g); i++) {
	for (j = 0; j < rank.get__(i).n; j++) {
	    v = (ST_Agnode_s) rank.get__(i).v.get_(j);
	    ND_coord(v).x = ND_rank(v);
	    ND_rank(v, i);
	}
    }
} finally {
LEAVING("1oobmglea9t819y95xeel37h8","set_xcoords");
}
}







/* clust_ht:
 * recursively compute cluster ht requirements.  assumes GD_ht1(subg) and ht2
 * are computed from primitive nodes only.  updates ht1 and ht2 to reflect
 * cluster nesting and labels.  also maintains global rank ht1 and ht2.
 * Return true if some cluster has a label.
 */
@Reviewed(when = "15/11/2020")
@Original(version="2.38.0", path="lib/dotgen/position.c", name="clust_ht", key="emtrqv582hdma5aajqtjd76m1", definition="static int clust_ht(Agraph_t * g)")
public static int clust_ht(Globals zz, ST_Agraph_s g) {
ENTERING("emtrqv582hdma5aajqtjd76m1","clust_ht");
try {
    int c;
    double ht1, ht2;
    ST_Agraph_s subg;
    CArray<ST_rank_t> rank = GD_rank(dot_root(g));
    int margin, haveClustLabel = 0;
    
    if (g == dot_root(g)) 
	margin = CL_OFFSET;
    else
	margin = late_int (g, zz.G_margin, CL_OFFSET, 0);
    
    ht1 = GD_ht1(g);
    ht2 = GD_ht2(g);
    
    /* account for sub-clusters */
    for (c = 1; c <= GD_n_cluster(g); c++) {
	subg = GD_clust(g).get_(c);
	haveClustLabel |= clust_ht(zz, subg);
	if (GD_maxrank(subg) == GD_maxrank(g))
	    ht1 = Math.max(ht1, GD_ht1(subg) + margin);
	if (GD_minrank(subg) == GD_minrank(g))
	    ht2 = Math.max(ht2, GD_ht2(subg) + margin);
    }
    
    /* account for a possible cluster label in clusters */
    /* room for root graph label is handled in dotneato_postprocess */
    if ((g != dot_root(g)) && GD_label(g)!=null) {
	haveClustLabel = 1;
	if (!GD_flip(agroot(g))) {
	    ht1 += GD_border(g)[BOTTOM_IX].y;
	    ht2 += GD_border(g)[TOP_IX].y;
	}
    }
    GD_ht1(g, ht1);
    GD_ht2(g, ht2);
    
    /* update the global ranks */
    if ((g != dot_root(g))) {
	rank.get__(GD_minrank(g)).ht2 = Math.max(rank.get__(GD_minrank(g)).ht2, ht2);
	rank.get__(GD_maxrank(g)).ht1 = Math.max(rank.get__(GD_maxrank(g)).ht1, ht1);
    }
    return haveClustLabel;
} finally {
LEAVING("emtrqv582hdma5aajqtjd76m1","clust_ht");
}
}




@Reviewed(when = "15/11/2020")
@Difficult
@Original(version="2.38.0", path="lib/dotgen/position.c", name="set_ycoords", key="bp8vmol4ncadervcfossysdtd", definition="static void set_ycoords(graph_t * g)")
public static void set_ycoords(Globals zz, ST_Agraph_s g) {
ENTERING("bp8vmol4ncadervcfossysdtd","set_ycoords");
try {
    int i, j, r;
    double ht2, maxht, delta, d0, d1;
    ST_Agnode_s n;
    ST_Agedge_s e;
    CArray<ST_rank_t> rank = GD_rank(g);
    ST_Agraph_s clust;
    int lbl;
    
    ht2 = maxht = 0;
    
    /* scan ranks for tallest nodes.  */
    for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	for (i = 0; i < rank.get__(r).n; i++) {
	    n = rank.get__(r).v.get_(i);
	    
	    /* assumes symmetry, ht1 = ht2 */
	    ht2 = ND_ht(n) / 2;
	    
	    
	    /* have to look for high self-edge labels, too */
	    if (ND_other(n).list!=null)
		for (j = 0; (e = (ST_Agedge_s) ND_other(n).list.get_(j))!=null; j++) {
		    if (agtail(e) == aghead(e)) {
			if (ED_label(e)!=null)
			    ht2 = Math.max(ht2, ED_label(e).dimen.y / 2);
		    }
		}
	    
	    /* update global rank ht */
	    if (rank.get__(r).pht2 < ht2) {
		rank.get__(r).ht2 = ht2;
		rank.get__(r).pht2 = ht2;
		}
	    if (rank.get__(r).pht1 < ht2) {
		rank.get__(r).ht1 = ht2;
		rank.get__(r).pht1 = ht2;
		}
	    
	    /* update nearest enclosing cluster rank ht */
	    if ((clust = ND_clust(n))!=null) {
		int yoff = (clust == g ? 0 : late_int (clust, zz.G_margin, CL_OFFSET, 0));
		if (ND_rank(n) == GD_minrank(clust))
		    GD_ht2(clust, Math.max(GD_ht2(clust), ht2 + yoff));
		if (ND_rank(n) == GD_maxrank(clust))
		    GD_ht1(clust, Math.max(GD_ht1(clust), ht2 + yoff));
	    }
	}
    }
    
    /* scan sub-clusters */
    lbl = clust_ht(zz, g);
    
    /* make the initial assignment of ycoords to leftmost nodes by ranks */
    maxht = 0;
    r = GD_maxrank(g);
    ND_coord(rank.get__(r).v.get_(0)).y = rank.get__(r).ht1;
    while (--r >= GD_minrank(g)) {
	d0 = rank.get__(r + 1).pht2 + rank.get__(r).pht1 + GD_ranksep(g);	/* prim node sep */
	d1 = rank.get__(r + 1).ht2 + rank.get__(r).ht1 + CL_OFFSET;	/* cluster sep */
	delta = Math.max(d0, d1);
	if (rank.get__(r).n > 0)	/* this may reflect some problem */
		ND_coord(rank.get__(r).v.get_(0)).y = (ND_coord(rank.get__(r + 1).v.get_(0))).y + delta;
	maxht = Math.max(maxht, delta);
    }
    
    /* If there are cluster labels and the drawing is rotated, we need special processing to
     * allocate enough room. We use adjustRanks for this, and then recompute the maxht if
     * the ranks are to be equally spaced. This seems simpler and appears to work better than
     * handling equal spacing as a special case.
     */
    if (lbl!=0 && GD_flip(g)) {
UNSUPPORTED("bxjqk5nu40mwo1156dicr9tur"); // 	adjustRanks(g, 0);
UNSUPPORTED("6vy9qfed3u61pmvy12724s9l4"); // 	if (GD_exact_ranksep(g)) {  /* recompute maxht */
UNSUPPORTED("74f5n6u4x39ngn0gsan7fgzyr"); // 	    maxht = 0;
UNSUPPORTED("2pd9g1n9b0746fgt892degls3"); // 	    r = GD_maxrank(g);
UNSUPPORTED("8dils3hlxottsbf2iuapvhqeq"); // 	    d0 = (ND_coord(rank[r].v[0])).y;
UNSUPPORTED("cw5accmrcan3lqfc789udgcka"); // 	    while (--r >= GD_minrank(g)) {
UNSUPPORTED("6bxo7bknt38qh9t31zr7p6kie"); // 		d1 = (ND_coord(rank[r].v[0])).y;
UNSUPPORTED("b1ta7vjm5i7swyklhfwy27w35"); // 		delta = d1 - d0;
UNSUPPORTED("65l8hg0imd48bfdu614k2kylt"); // 		maxht = MAX(maxht, delta);
UNSUPPORTED("5irf6cp6xdzi2ik033azsbauo"); // 		d0 = d1;
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("flupwh3kosf3fkhkxllllt1"); // 	}
    }
    
    /* re-assign if ranks are equally spaced */
    if (GD_exact_ranksep(g)!=0) {
UNSUPPORTED("cyxbyjrdzywkc46nl8lkrngai"); // 	for (r = GD_maxrank(g) - 1; r >= GD_minrank(g); r--)
UNSUPPORTED("5sd5ltavyp6llt0t2t0xmqwj5"); // 	    if (rank[r].n > 0)	/* this may reflect the same problem :-() */
UNSUPPORTED("5zoeqpznt31feqxjcx2rg0o1f"); // 			(ND_coord(rank[r].v[0])).y=
UNSUPPORTED("e6dfx5uesysjaefb0djyfp7f"); // 		    (ND_coord(rank[r + 1].v[0])).y + maxht;
    }
    
    /* copy ycoord assignment from leftmost nodes to others */
    for (n = GD_nlist(g); n!=null; n = ND_next(n))
    ND_coord(n).y = ND_coord(rank.get__(ND_rank(n)).v.get_(0)).y;
} finally {
LEAVING("bp8vmol4ncadervcfossysdtd","set_ycoords");
}
}




/* dot_compute_bb:
 * Compute bounding box of g.
 * The x limits of clusters are given by the x positions of ln and rn.
 * This information is stored in the rank field, since it was calculated
 * using network simplex.
 * For the root graph, we don't enforce all the constraints on lr and 
 * rn, so we traverse the nodes and subclusters.
 */
//3 9ay2xnnmh407i32pfokujfda5
//static void dot_compute_bb(graph_t * g, graph_t * root) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="dot_compute_bb", key="9ay2xnnmh407i32pfokujfda5", definition="tatic void dot_compute_bb(graph_t * g, graph_t * root)")
public static void dot_compute_bb(ST_Agraph_s g, ST_Agraph_s root) {
ENTERING("9ay2xnnmh407i32pfokujfda5","dot_compute_bb");
try {
 int r, c;
 double x, offset;
 ST_Agnode_s v;
 final ST_pointf LL = new ST_pointf();
 final ST_pointf UR = new ST_pointf();
 if (g == dot_root(g)) {
	LL.x = INT_MAX;
	UR.x = -((double)INT_MAX);
	for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	    int rnkn = GD_rank(g).get__(r).n;
	    if (rnkn == 0)
		continue;
	    if ((v = (ST_Agnode_s) GD_rank(g).get__(r).v.get_(0)) == null)
		continue;
	    for (c = 1; (ND_node_type(v) != 0) && c < rnkn; c++)
		v = (ST_Agnode_s) GD_rank(g).get__(r).v.get_(c);
	    if (ND_node_type(v) == 0) {
		x = ND_coord(v).x - ND_lw(v);
		LL.x = (Math.min(LL.x, x));
	    }
	    else continue;
		/* At this point, we know the rank contains a NORMAL node */
	    v = (ST_Agnode_s) GD_rank(g).get__(r).v.get_(rnkn - 1);
	    for (c = rnkn-2; ND_node_type(v) != 0; c--)
		v = (ST_Agnode_s) GD_rank(g).get__(r).v.get_(c);
	    x = ND_coord(v).x + ND_rw(v);
	    UR.x = Math.max(UR.x, x);
	}
	offset = 8;
	for (c = 1; c <= GD_n_cluster(g); c++) {
	    x = (double)(GD_bb(GD_clust(g).get_(c)).LL.x - offset);
	    LL.x = Math.min(LL.x, x);
	    x = (double)(GD_bb(GD_clust(g).get_(c)).UR.x + offset);
	    UR.x = Math.max(UR.x, x);
	}
 } else {
	LL.x = (double)(ND_rank(GD_ln(g)));
	UR.x = (double)(ND_rank(GD_rn(g)));
 }
 LL.y = ND_coord(GD_rank(root).get__(GD_maxrank(g)).v.get_(0)).y - GD_ht1(g);
 UR.y = ND_coord(GD_rank(root).get__(GD_minrank(g)).v.get_(0)).y + GD_ht2(g);
 GD_bb(g).LL.___(LL);
 GD_bb(g).UR.___(UR);
} finally {
LEAVING("9ay2xnnmh407i32pfokujfda5","dot_compute_bb");
}
}





//3 dlbpiimh9g9ff9w7wjoabf817
// static void rec_bb(graph_t * g, graph_t * root) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="rec_bb", key="dlbpiimh9g9ff9w7wjoabf817", definition="static void rec_bb(graph_t * g, graph_t * root)")
public static void rec_bb(ST_Agraph_s g, ST_Agraph_s root) {
ENTERING("dlbpiimh9g9ff9w7wjoabf817","rec_bb");
try {
    int c;
    for (c = 1; c <= GD_n_cluster(g); c++)
	rec_bb((ST_Agraph_s) GD_clust(g).get_(c), root);
    dot_compute_bb(g, root);
} finally {
LEAVING("dlbpiimh9g9ff9w7wjoabf817","rec_bb");
}
}






//3 53z9yhpfixulhgqsauulkllvc
// static void adjustAspectRatio (graph_t* g, aspect_t* asp) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="adjustAspectRatio", key="53z9yhpfixulhgqsauulkllvc", definition="static void adjustAspectRatio (graph_t* g, aspect_t* asp)")
public static Object adjustAspectRatio(Object... arg_) {
UNSUPPORTED("ezldb6r0csirv1fmkq5itw1v2"); // static void adjustAspectRatio (graph_t* g, aspect_t* asp)
UNSUPPORTED("erg9i1970wdri39osu8hx2a6e"); // {
UNSUPPORTED("7rtldqefvdgcf4u50kulbxtvn"); //     double AR = (GD_bb(g).UR.x - GD_bb(g).LL.x)/(GD_bb(g).UR.y - GD_bb(g).LL.y);
UNSUPPORTED("cve2on8gll5i0vomy8lnwhai2"); //     if (Verbose) {
UNSUPPORTED("6z0q1m3yc6o11ejsa59eghqag"); //         fprintf(stderr, "AR=%0.4lf\t Area= %0.4lf\t", AR, (double)(GD_bb(g).UR.x - GD_bb(g).LL.x)*(GD_bb(g).UR.y - GD_bb(g).LL.y)/10000.0);
UNSUPPORTED("2uealcdkjdgg8ne1cijkbagpu"); //         fprintf(stderr, "Dummy=%d\n", countDummyNodes(g));
UNSUPPORTED("dvgyxsnyeqqnyzq696k3vskib"); //     }
UNSUPPORTED("96mqnzcfbfghxkxkn1x8kdh8i"); //     if (AR > 1.1*asp->targetAR) {
UNSUPPORTED("6gq7uj15zh138zyae50c8nh2z"); //       asp->nextIter = (int)(asp->targetAR * (double)(asp->curIterations - asp->prevIterations)/(AR));
UNSUPPORTED("dvgyxsnyeqqnyzq696k3vskib"); //     }
UNSUPPORTED("e1443w2cx49ogpsf5m59zy4fq"); //     else if (AR <= 0.8 * asp->targetAR) {
UNSUPPORTED("5awa0x1pxpta5wou27bzrtvoc"); //       asp->nextIter = -1;
UNSUPPORTED("5xdo0sx20rmxgmdkrm1giaige"); //       if (Verbose)
UNSUPPORTED("du5ztjo6nfo54ailmk1tqs05b"); //         fprintf(stderr, "Going to apply another expansion.\n");
UNSUPPORTED("dvgyxsnyeqqnyzq696k3vskib"); //     }
UNSUPPORTED("1nyzbeonram6636b1w955bypn"); //     else {
UNSUPPORTED("757eq4638npmb5w5e39iemxfo"); // 	asp->nextIter = 0;
UNSUPPORTED("dvgyxsnyeqqnyzq696k3vskib"); //     }
UNSUPPORTED("2di5wqm6caczzl6bvqe35ry8y"); //     if (Verbose)
UNSUPPORTED("29wdml7g4931q8kgah8hgwjd0"); //         fprintf(stderr, "next#iter=%d\n", asp->nextIter);
UNSUPPORTED("c24nfmv9i7o5eoqaymbibp7m7"); // }

throw new UnsupportedOperationException();
}




//3 7effq6z6ur101wrch6ttozr26
// static void set_aspect(graph_t * g, aspect_t* asp) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="set_aspect", key="7effq6z6ur101wrch6ttozr26", definition="static void set_aspect(graph_t * g, aspect_t* asp)")
public static void set_aspect(ST_Agraph_s g, ST_aspect_t asp) {
ENTERING("7effq6z6ur101wrch6ttozr26","set_aspect");
try {
    double xf = 0.0, yf = 0.0, actual, desired;
    ST_Agnode_s n;
    boolean scale_it, filled;
    ST_point sz = new ST_point();
    rec_bb(g, g);
    if ((GD_maxrank(g) > 0) && (GD_drawing(g).ratio_kind!=EN_ratio_t.R_NONE)) {
UNSUPPORTED("5wbmy4x78flo2ztfabki9lyjf"); // 	sz.x = GD_bb(g).UR.x - GD_bb(g).LL.x;
UNSUPPORTED("catd6eu5oc282ln95k9zz52f3"); // 	sz.y = GD_bb(g).UR.y - GD_bb(g).LL.y;	/* normalize */
UNSUPPORTED("21zvq2qx1j34j1i1879zyhzpj"); // 	if (GD_flip(g)) {
UNSUPPORTED("d55uzald1tvs7xodnua67pxv6"); // 	    int t = sz.x;
UNSUPPORTED("47s1klx0pfzda4e311w53ou7e"); // 	    sz.x = sz.y;
UNSUPPORTED("3tx1mj7j0rqw33y24a0gu4ali"); // 	    sz.y = t;
UNSUPPORTED("flupwh3kosf3fkhkxllllt1"); // 	}
UNSUPPORTED("4ct8ztqxnsskgphsp1v3aw5ec"); // 	scale_it = NOT(0);
UNSUPPORTED("8v772n1u4wqlmskqfswlzcz2o"); // 	if (GD_drawing(g)->ratio_kind == R_AUTO)
UNSUPPORTED("8mtmgag5dxj8ttlcabbpd865p"); // 	    filled = idealsize(g, .5);
UNSUPPORTED("9352ql3e58qs4fzapgjfrms2s"); // 	else
UNSUPPORTED("bxtk5e5ls8qsd36eucvhufg9y"); // 	    filled = GD_drawing(g)->ratio_kind == R_FILL;
UNSUPPORTED("2atgu691bmn6h9jvk8lve5qzc"); // 	if (filled) {
UNSUPPORTED("1zx5etcjofceqjvogfn8urkdj"); // 	    /* fill is weird because both X and Y can stretch */
UNSUPPORTED("7ezqjon4u21dwg4qvxssrnwfc"); // 	    if (GD_drawing(g)->size.x <= 0)
UNSUPPORTED("dao0fnoi65upcdtr9csqhhy41"); // 		scale_it = 0;
UNSUPPORTED("6q044im7742qhglc4553noina"); // 	    else {
UNSUPPORTED("6ifkww34s7php908n9wg0oiju"); // 		xf = (double) GD_drawing(g)->size.x / (double) sz.x;
UNSUPPORTED("6faiikvxwdge2ydblv90976hb"); // 		yf = (double) GD_drawing(g)->size.y / (double) sz.y;
UNSUPPORTED("5xkzvdrdnfd5afhhxgajbywya"); // 		if ((xf < 1.0) || (yf < 1.0)) {
UNSUPPORTED("cvkvyq93xp1itpomhj1r2xlzy"); // 		    if (xf < yf) {
UNSUPPORTED("capfpf4tncicsp81elmwvf0l"); // 			yf = yf / xf;
UNSUPPORTED("7sdzyzqj65rbq6edfgf5x6xht"); // 			xf = 1.0;
UNSUPPORTED("d86r93g8nz9a1kfzgi7f8j8nh"); // 		    } else {
UNSUPPORTED("emwaipsi6kyqbpk2y26k3cxfw"); // 			xf = xf / yf;
UNSUPPORTED("1s91x56ftedjsc3m32dqgspqn"); // 			yf = 1.0;
UNSUPPORTED("dkxvw03k2gg9anv4dbze06axd"); // 		    }
UNSUPPORTED("6eq5kf0bj692bokt0bixy1ixh"); // 		}
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("c48w89y9jw5baxqqucmiyfha7"); // 	} else if (GD_drawing(g)->ratio_kind == R_EXPAND) {
UNSUPPORTED("7ezqjon4u21dwg4qvxssrnwfc"); // 	    if (GD_drawing(g)->size.x <= 0)
UNSUPPORTED("dao0fnoi65upcdtr9csqhhy41"); // 		scale_it = 0;
UNSUPPORTED("6q044im7742qhglc4553noina"); // 	    else {
UNSUPPORTED("akfs904fsk7cyl8wbv0x7fnvz"); // 		xf = (double) GD_drawing(g)->size.x /
UNSUPPORTED("1ewqbc4kglc2kg1n13euxrxzh"); // 		    (double) GD_bb(g).UR.x;
UNSUPPORTED("6fmkpg9ypaxceugi24gklvdra"); // 		yf = (double) GD_drawing(g)->size.y /
UNSUPPORTED("8mskb0mqou89myfbiihsjpbg6"); // 		    (double) GD_bb(g).UR.y;
UNSUPPORTED("a2jzgqbpoanzqdqc7vjk32vmz"); // 		if ((xf > 1.0) && (yf > 1.0)) {
UNSUPPORTED("6np8qfg5qnlaypikhw0bdx84j"); // 		    double scale = MIN(xf, yf);
UNSUPPORTED("272bmuv1row7l9tla5bhot840"); // 		    xf = yf = scale;
UNSUPPORTED("738mi6h8ef0itznt34ngxe25o"); // 		} else
UNSUPPORTED("b5qs3ho2fcywk3sd5cw3m88kw"); // 		    scale_it = 0;
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("d7deewhp6akdb13j7ry364hbd"); // 	} else if (GD_drawing(g)->ratio_kind == R_VALUE) {
UNSUPPORTED("fpejwrlknxizaxxibhuyaxdt"); // 	    desired = GD_drawing(g)->ratio;
UNSUPPORTED("douwz2voka0puoeooqjn8kijk"); // 	    actual = ((double) sz.y) / ((double) sz.x);
UNSUPPORTED("7u9yvuqazzo19geppiphd9rfh"); // 	    if (actual < desired) {
UNSUPPORTED("apqq3m2rezfl96zbvk7lut02"); // 		yf = desired / actual;
UNSUPPORTED("8po0oizki4figodjv9xku16gq"); // 		xf = 1.0;
UNSUPPORTED("175pyfe8j8mbhdwvrbx3gmew9"); // 	    } else {
UNSUPPORTED("84c3pp9xgnii11clyyxblqmy6"); // 		xf = actual / desired;
UNSUPPORTED("1tr1b9rp1b00pcafss87kadfe"); // 		yf = 1.0;
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("6to1esmb8qfrhzgtr7jdqleja"); // 	} else
UNSUPPORTED("csvnhx5mo535o6ue1tg3ktjhs"); // 	    scale_it = 0;
UNSUPPORTED("bh2d68e9s7cr7k1bl0h9fmr9a"); // 	if (scale_it) {
UNSUPPORTED("b8symsgdtoq84y3j1151pv0g4"); // 	    if (GD_flip(g)) {
UNSUPPORTED("bi983gfofc0blj8r4yetj14kb"); // 		double t = xf;
UNSUPPORTED("69pmocxfvmk0urni4fg0x4na5"); // 		xf = yf;
UNSUPPORTED("cyt895z1pa5arxz4d1kv0hqgq"); // 		yf = t;
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("crtcqz91ff5l8ntjbne40b5x4"); // 	    for (n = GD_nlist(g); n; n = ND_next(n)) {
UNSUPPORTED("brs6nych5z9m0a75ixbe5l80o"); // 		ND_coord(n).x = ROUND(ND_coord(n).x * xf);
UNSUPPORTED("cpe0pjsilppgrp2ofysn4y54w"); // 		ND_coord(n).y = ROUND(ND_coord(n).y * yf);
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("8f0d3etdet1pk8ikvltmz5h2s"); // 	    scale_bb(g, g, xf, yf);
UNSUPPORTED("flupwh3kosf3fkhkxllllt1"); // 	}
    }
    if (asp!=null) adjustAspectRatio (g, asp);
} finally {
LEAVING("7effq6z6ur101wrch6ttozr26","set_aspect");
}
}




/* make space for the leaf nodes of each rank */
@Unused
@Reviewed(when = "16/11/2020")
@Original(version="2.38.0", path="lib/dotgen/position.c", name="make_leafslots", key="1lejhh3evsa10auyj7cgqj8ub", definition="static void make_leafslots(graph_t * g)")
public static void make_leafslots(ST_Agraph_s g) {
ENTERING("1lejhh3evsa10auyj7cgqj8ub","make_leafslots");
try {
    int i, j, r;
    ST_Agnode_s v;
    
    for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	j = 0;
	for (i = 0; i < GD_rank(g).get__(r).n; i++) {
	    v = GD_rank(g).get__(r).v.get_(i);
	    ND_order(v, j);
	    if (ND_ranktype(v) == LEAFSET)
		j = j + ND_UF_size(v);
	    else
		j++;
	}
	if (j <= GD_rank(g).get__(r).n)
	    continue;
	GD_rank(g).get__(r).v = CArrayOfStar.<ST_Agnode_s>REALLOC(j + 1, GD_rank(g).get__(r).v, ZType.ST_Agnode_s);
	for (i = GD_rank(g).get__(r).n - 1; i >= 0; i--) {
	    v = GD_rank(g).get__(r).v.get_(i);
	    GD_rank(g).get__(r).v.set_(ND_order(v), v);
	}
	GD_rank(g).get__(r).n = j;
	GD_rank(g).get__(r).v.set_(j, null);
    }
} finally {
LEAVING("1lejhh3evsa10auyj7cgqj8ub","make_leafslots");
}
}




//3 wb2wvxthkr2sp9u8113go3j3
// static void do_leaves(graph_t * g, node_t * leader) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="do_leaves", key="wb2wvxthkr2sp9u8113go3j3", definition="static void do_leaves(graph_t * g, node_t * leader)")
public static Object do_leaves(Object... arg_) {
UNSUPPORTED("5nmyuqyhfqfwbmgdj5aot9fp4"); // static void do_leaves(graph_t * g, node_t * leader)
UNSUPPORTED("erg9i1970wdri39osu8hx2a6e"); // {
UNSUPPORTED("2bs0wcp6367dz1o5x166ec7l8"); //     int j;
UNSUPPORTED("4bxv0pw87c31sbbz6x6s1cq9d"); //     point lbound;
UNSUPPORTED("cjx5v6hayed3q8eeub1cggqca"); //     node_t *n;
UNSUPPORTED("5gypxs09iuryx5a2eho9lgdcp"); //     edge_t *e;
UNSUPPORTED("b9upgllg8zjx49090hr3afv91"); //     if (ND_UF_size(leader) <= 1)
UNSUPPORTED("a7fgam0j0jm7bar0mblsv3no4"); // 	return;
UNSUPPORTED("9m0hiwybw3dr0lcxmgq833heo"); //     lbound.x = ND_coord(leader).x - ND_lw(leader);
UNSUPPORTED("ev0phf24gpqz3xtvtueq72f7g"); //     lbound.y = ND_coord(leader).y;
UNSUPPORTED("90r9xqe4faj7b1g8907ord1x3"); //     lbound = resize_leaf(leader, lbound);
UNSUPPORTED("66ue8mvk3axhgbkcg3xqo94tb"); //     if (ND_out(leader).size > 0) {	/* in-edge leaves */
UNSUPPORTED("9lq5udq73fgfeqyqsxw6i3pgm"); // 	n = aghead(ND_out(leader).list[0]);
UNSUPPORTED("4vp9ny4udt1jcmibfgpwgrnqo"); // 	j = ND_order(leader) + 1;
UNSUPPORTED("3ml0tasns5tz6d5xc2xdb6nc"); // 	for (e = agfstin(g, n); e; e = agnxtin(g, e)) {
UNSUPPORTED("e2y71fdc15yxylowp6ohlal9a"); // 	    edge_t *e1 = AGMKOUT(e);
UNSUPPORTED("ew4udmdawt257gbk5kzmi1n1"); // 	    if ((agtail(e1) != leader) && (UF_find(agtail(e1)) == leader)) {
UNSUPPORTED("d2r94m7xk4qa9hn6s2td5nb6h"); // 		lbound = place_leaf(g, agtail(e1), lbound, j++);
UNSUPPORTED("5xk9d4ra447xucksge6c9mgos"); // 		unmerge_oneway(e1);
UNSUPPORTED("4dqun0n52lcyerkebva2hxh15"); // 		elist_append(e1, ND_in(aghead(e1)));
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("flupwh3kosf3fkhkxllllt1"); // 	}
UNSUPPORTED("6owoaz61uf3928omhuf3rqjoa"); //     } else {			/* out edge leaves */
UNSUPPORTED("4lbkem8hb4jy3gdg6nt3cbtl9"); // 	n = agtail(ND_in(leader).list[0]);
UNSUPPORTED("4vp9ny4udt1jcmibfgpwgrnqo"); // 	j = ND_order(leader) + 1;
UNSUPPORTED("e20lm4qtccvgsfq5fzjv6sjyl"); // 	for (e = agfstout(g, n); e; e = agnxtout(g, e)) {
UNSUPPORTED("38a98cy0214odvsa98hgyce8q"); // 	    if ((aghead(e) != leader) && (UF_find(aghead(e)) == leader)) {
UNSUPPORTED("9j9o79r2bdc6npidu38aq0cym"); // 		lbound = place_leaf(g, aghead(e), lbound, j++);
UNSUPPORTED("6wbwzuqqh5vxume7ga2kuejcf"); // 		unmerge_oneway(e);
UNSUPPORTED("dv9vv9pfcd3cibfjn258toxyv"); // 		elist_append(e, ND_out(agtail(e)));
UNSUPPORTED("6t98dcecgbvbvtpycwiq2ynnj"); // 	    }
UNSUPPORTED("flupwh3kosf3fkhkxllllt1"); // 	}
UNSUPPORTED("dvgyxsnyeqqnyzq696k3vskib"); //     }
UNSUPPORTED("c24nfmv9i7o5eoqaymbibp7m7"); // }

throw new UnsupportedOperationException();
}




//3 alpljm8o6nsam95ly6leelnbp
// int ports_eq(edge_t * e, edge_t * f) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="ports_eq", key="alpljm8o6nsam95ly6leelnbp", definition="int ports_eq(edge_t * e, edge_t * f)")
public static boolean ports_eq(ST_Agedge_s e, ST_Agedge_s f) {
ENTERING("alpljm8o6nsam95ly6leelnbp","ports_eq");
try {
    return ((ED_head_port(e).defined == ED_head_port(f).defined)
	    && (((ED_head_port(e).p.x == ED_head_port(f).p.x) &&
		 (ED_head_port(e).p.y == ED_head_port(f).p.y))
		|| (ED_head_port(e).defined == false))
	    && (((ED_tail_port(e).p.x == ED_tail_port(f).p.x) &&
		 (ED_tail_port(e).p.y == ED_tail_port(f).p.y))
		|| (ED_tail_port(e).defined == false))
	);
} finally {
LEAVING("alpljm8o6nsam95ly6leelnbp","ports_eq");
}
}




@Difficult
@Todo(what = "review zapinlist(&(ND_other(n)), e);")
@Reviewed(when = "16/11/2020")
@Original(version="2.38.0", path="lib/dotgen/position.c", name="expand_leaves", key="cfotmdif5xv7n6oauyvzv0qwa", definition="static void expand_leaves(graph_t * g)")
public static void expand_leaves(ST_Agraph_s g) {
ENTERING("cfotmdif5xv7n6oauyvzv0qwa","expand_leaves");
try {
    int i, d;
    ST_Agnode_s n;
    ST_Agedge_s e, f;
    
    make_leafslots(g);
    for (n = GD_nlist(g); n!=null; n = ND_next(n)) {
	if (ND_inleaf(n)!=null)
	    do_leaves(g, ND_inleaf(n));
	if (ND_outleaf(n)!=null)
	    do_leaves(g, ND_outleaf(n));
	if (ND_other(n).list!=null)
	    for (i = 0; (e = (ST_Agedge_s) ND_other(n).list.get_(i))!=null; i++) {
		if ((d = ND_rank(aghead(e)) - ND_rank(aghead(e))) == 0)
		    continue;
		f = ED_to_orig(e);
		if (ports_eq(e, f) == false) {
			UNSURE_ABOUT("zapinlist(&(ND_other(n)), e);");
		    zapinlist(ND_other(n), e);
		    if (d == 1)
			fast_edge(e);
		    /*else unitize(e); ### */
		    i--;
		}
	    }
    }
} finally {
LEAVING("cfotmdif5xv7n6oauyvzv0qwa","expand_leaves");
}
}




//3 d4b57ugpwxy567pfmxn14ed8d
// static void make_lrvn(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="make_lrvn", key="d4b57ugpwxy567pfmxn14ed8d", definition="static void make_lrvn(graph_t * g)")
public static void make_lrvn(ST_Agraph_s g) {
ENTERING("d4b57ugpwxy567pfmxn14ed8d","make_lrvn");
try {
    ST_Agnode_s ln, rn;
    if (GD_ln(g)!=null)
	return;
    ln = virtual_node(dot_root(g));
    ND_node_type(ln, 2);
    rn = virtual_node(dot_root(g));
    ND_node_type(rn, 2);
    if (GD_label(g)!=null && (g != dot_root(g)) && !GD_flip(agroot(g))) {
	int w = Math.max((int)GD_border(g)[0].x, (int)GD_border(g)[2].x);
	make_aux_edge(ln, rn, w, 0);
    }
    GD_ln(g, ln);
    GD_rn(g, rn);
} finally {
LEAVING("d4b57ugpwxy567pfmxn14ed8d","make_lrvn");
}
}




//3 daz786541idcxnywckcbncazb
// static void contain_nodes(graph_t * g) 
@Unused
@Original(version="2.38.0", path="lib/dotgen/position.c", name="contain_nodes", key="daz786541idcxnywckcbncazb", definition="static void contain_nodes(graph_t * g)")
public static void contain_nodes(Globals zz, ST_Agraph_s  g) {
ENTERING("daz786541idcxnywckcbncazb","contain_nodes");
try {
    int margin, r;
    ST_Agnode_s ln, rn, v;
    margin = late_int (g, zz.G_margin, 8, 0);
    make_lrvn(g);
    ln = GD_ln(g);
    rn = GD_rn(g);
    for (r = GD_minrank(g); r <= GD_maxrank(g); r++) {
	if (GD_rank(g).get__(r).n == 0)
	    continue;
	v = GD_rank(g).get__(r).v.get_(0);
	if (v == null) {
UNSUPPORTED("1f2esoodtcrdhljk1cq1klyao"); // 	    agerr(AGERR, "contain_nodes clust %s rank %d missing node\n",
UNSUPPORTED("7w6lv4ywtczwz2y1mg0p3jdav"); // 		  agnameof(g), r);
UNSUPPORTED("6hqli9m8yickz1ox1qfgtdbnd"); // 	    continue;
	}
	make_aux_edge(ln, v,
		      ND_lw(v) + margin + GD_border(g)[3].x, 0);
	v = GD_rank(g).get__(r).v.get_(GD_rank(g).get__(r).n - 1);
	make_aux_edge(v, rn,
		      ND_rw(v) + margin + GD_border(g)[1].x, 0);
    }
} finally {
LEAVING("daz786541idcxnywckcbncazb","contain_nodes");
}
}


}
