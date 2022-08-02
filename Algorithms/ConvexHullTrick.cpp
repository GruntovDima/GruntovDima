/// Task: https://codeforces.com/contest/932/problem/F

/*
    Hruntou Dmitry
    GNU C++ 14
*/


#include<bits/stdc++.h>
#include<ext/pb_ds/assoc_container.hpp>
#include<ext/pb_ds/tree_policy.hpp>

using namespace __gnu_pbds;
using namespace std;

#define ll  long long
#define int long long
#define pb push_back
#define F first
#define S second
#define ld long double
#define pii pair<int,int>
#define tip  pii
#define all(x) (x).begin(),(x).end()

typedef tree<
tip,
null_type,
less < tip >,
rb_tree_tag,
tree_order_statistics_node_update> ordered_set;

mt19937 gen(chrono::high_resolution_clock::now().time_since_epoch().count());

const ll inf = 1e18 + 100;
const size_t N = 1e5+ 100;

int a[N];
int b[N];
int ans[N];
vector < int > g[N];
ll n;

struct Line{
   mutable  int k, b, p;
    Line(int x1, int x2, int x3){
        k = x1;
        b = x2;
        p = x3;
    }
    bool operator < (const Line &a)
    const{
        return k < a.k;
        }
    bool operator < (int x)
    const{
            return p < x;
        }
};
struct CHT : multiset < Line , less <>>{
    ll div(int a, int b){
        return a/b - ((a ^ b) < 0 && a % b != 0); /// right round whin div
    }
    ll query(int x){
        if(empty()) return 0;
        auto ans =  *lower_bound(x);   /// last line that (previos and line)-> y < x
        return -ans.k * x - ans.b;
    }
    bool change(iterator a, iterator b){
        if(b == end()){
            a -> p = inf; return false;
        }
        if(a -> k == b -> k){
            a -> p = (a -> b > b -> b ? inf : -inf);
        }else{
            a -> p = div(b -> b - a -> b, a -> k - b -> k);
        }
        return a -> p >= b -> p;
    }
    void add(int k, int b){
        k = -k;
        b = -b;
        auto z = insert(Line(k, b, 0)); /// ADD NEW LINE
        auto y = z++;
        auto x = y;
        while(change(y, z) && z != end()) change(y,z = erase(z)); /// DELETE ALL TRASH LINES AFTER NEW (TRASH LINE IS LINE THAT DID'T GIVE ANS FOR ANY X)
        if((x = y)!=begin() && change(--x, y)){ /// CHECK IF NEW LINE IS A TRASH LINE
            change(x, y = erase(y));
        }
        while((y = x) != begin() && change(--x, y)){ /// DELETE ALL TRASH LINES BEFORE NEW
                change(x, erase(y));
        }
    }
};

CHT Hull[N];


void  dfs(int v = 0, int pr = 0){   /// SOLVE
    for(auto to : g[v])
        if(to != pr)
            dfs(to, v);
    for(auto to : g[v]){
        if(to == pr) continue;
        for(auto to1 : Hull[to])
            Hull[v].add(-to1.k, -to1.b);
    }
    ans[v] = Hull[v].query(a[v]);
    Hull[v].add(b[v], ans[v]);
}

main() {
    ios_base::sync_with_stdio(0);
    cin.tie(0);
    cout.tie(0);
    cin >> n;
    for(int i = 0;i < n; i++)
        cin >> a[i];
    for(int i = 0;i < n; i++)
        cin >> b[i];
    for(int i = 0;i < n - 1; i++) { /// create graph list
        ll u, v;
        cin >> u >> v;
        u--;
        v--;
        g[u].pb(v);
        g[v].pb(u);
    }
    dfs();
    for(int i = 0;i <  n; i++){
        cout << ans[i] << " ";
    }
    return 0;
}
