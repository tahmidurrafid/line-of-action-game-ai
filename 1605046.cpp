#include<bits/stdc++.h>
using namespace std;
#define posType pair<int , int>
#define moveType pair<posType, posType>
#define Terminal 100000000
int n;
int maxLevel = 3;

int pieceSquareTable[8][8] =
{
-80, -25, -20, -20, -20, -20, -25, -80,
-25,  10,  10,  10,  10,  10,  10,  -25,
-20,  10,  25,  25,  25,  25,  10,  -20,
-20,  10,  25,  50,  50,  25,  10,  -20,
-20,  10,  25,  50,  50,  25,  10,  -20,
-20,  10,  25,  25,  25,  25,  10,  -20,
-25,  10,  10,  10,  10,  10,  10,  -25,
-80, -25, -20, -20, -20, -20, -25, -80
};

pair<int, int> posOnDir(pair<int, int> pos, pair<int, int> dim, int turn, vector< vector<int>> state){
    int counter = -1;
    int x = pos.first, y = pos.second;
    while(x < n && x >= 0 && y < n && y >= 0){
        counter += state[x][y] != 0? 1 : 0;
        x += dim.first;
        y += dim.second;
    }
    x = pos.first;
    y = pos.second;
    while(x < n && x >= 0 && y < n && y >= 0){
        counter += state[x][y] != 0? 1 : 0;
        x -= dim.first;
        y -= dim.second;
    }
    counter--;
    x = pos.first + dim.first;
    y = pos.second + dim.second;
    while(x < n && x >= 0 && y < n && y >= 0){
        if(counter == 0 && state[x][y] != turn){
            return {x, y};
        }
        if(state[x][y] == -turn) return {-1, -1};
        counter--;
        x += dim.first;
        y += dim.second;
    }
    return {-1 ,-1};
}

void dfsUtil(int x, int y, vector<vector<int>> &state, vector<vector<bool>> &visited, int turn){
    if(x < 0 || x >= n || y < 0 || y >= n || visited[x][y] || state[x][y] != turn) return;
    visited[x][y] = true;
    for(int i = -1; i <= 1; i++){
        for(int j = -1; j <= 1; j++){
            if(i == 0 && j == 0) continue;
            dfsUtil(x+i, y+j, state, visited, turn);
        }
    }
}

int utility(vector<vector<int>> state){
    vector< vector<bool>> visited(n, vector<bool>(n, false));
    int one =0, mone = 0;
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == 1 && visited[i][j] == false){
                one++;
                dfsUtil(i, j, state, visited, 1);
            }
        }
    }
    visited = vector< vector<bool>>(n, vector<bool>(n, false));
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == -1 && visited[i][j] == false){
                mone++;
                dfsUtil(i, j, state, visited, -1);
            }
        }
    }
    if(one == 1 && mone == 1) return 2;
    if(one == 1) return 1;
    if(mone == 1) return -1;
    return 0;
}

vector< moveType > successors(vector<vector<int>> const &state, int turn){
    vector< pair<pair<int, int>, pair<int, int>> > options;
    for(int x = 0; x < n; x++){
        for(int y = 0; y < n; y++){
            if(state[x][y] == turn){
                for(int i = -1; i <= 1; i++){
                    for(int j = -1; j <= 1; j++){
                        if(i == 0 && j == 0) continue;
                        pair<int, int> option = posOnDir({x, y}, {i, j}, turn, state);
                        if(option.first != -1){
                            options.push_back({{x, y}, option});
                        }
                    }
                }
            }            
        }
    }
    return options;
}

int peiceSquare(vector<vector<int>> const &state){
    int val = 0;
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            val += pieceSquareTable[i][j] * state[i][j];
        }
    }
    return -val;
}

int area(vector<vector<int>> const &state){
    pair<int, int> tl, br;
    tl = {n, n}; 
    br = {-1, -1};
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == 1){
                tl.first = min(tl.first, i);
                tl.second = min(tl.second, j);
                br.first = max(br.first, i);
                br.second = max(br.second, j);
            }
        }
    }
    int val = (br.first - tl.first)*(br.second - tl.second);
    tl = {n, n}; 
    br = {-1, -1};
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == -1){
                tl.first = min(tl.first, i);
                tl.second = min(tl.second, j);
                br.first = max(br.first, i);
                br.second = max(br.second, j);
            }
        }
    }
    int res = val;
    val = (br.first - tl.first)*(br.second - tl.second);
    res -= val;
    return res;
}

int quadCount(vector<vector<int>> const &state){
    int one = 0, mone = 0;
    for(int i = 0; i < n-1; i++){
        for(int j = 0; j < n-1; j++){
            int fOne = 0, fMone = 0;
            fOne = (state[i][j] == 1) + (state[i+1][j] == 1) + (state[i][j+1] == 1) + (state[i+1][j+1] == 1);            
            fMone = (state[i][j] == -1) + (state[i+1][j] == -1) + (state[i][j+1] == -1) + (state[i+1][j+1] == -1);
            if(fOne >= 3) one++;
            if(fMone >= 3) mone++;
        }
    }
    return mone - one;
}

int calcDistance(vector<vector<int>> const &state, int turn){
    int centerx = 0, centery = 0;
    int counter = 0;
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == turn){
                centerx += i;
                centery += j; 
                counter++;
            }
        }
    }
    centerx = centerx/counter;
    centery = centery/counter;
    int dist = 0;
    int res = 0;
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] == turn){
                dist += max(abs(centerx - i), abs(centery - j));
            }
        }
    }
    return dist;
}

int density(vector<vector<int>> const &state){
    return calcDistance(state, 1) - calcDistance(state, -1);
}

int connectedness(vector<vector<int>> const &state){
    int mOne = 0, one = 0;
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            if(state[i][j] != 0){
                for(int x = -1; x <= 1; x++){
                    for(int y = -1; y <= 1; y++){
                        if(x == 0 && y == 0) continue;
                        int posX = i+x, posY = j+y;
                        if(posX >= 0 && posX < n && posY >= 0 && posY < n){
                            if(state[posX][posY] == state[i][j]){
                                if(state[i][j]  == -1) mOne++;
                                else one++;
                            }
                        }
                    }
                }
            }
        }
    }
    return mOne - one;
}

int evaluate(vector<vector<int>> const &state){
    return 100*density(state) 
            + peiceSquare(state) 
            + area(state) 
            + quadCount(state)
            + connectedness(state);
    ;
}

void moveIt(vector<vector<int>> &state, moveType move){
    state[move.second.first][move.second.second] = state[move.first.first][move.first.second];
    state[move.first.first][move.first.second] = 0;
}

void seachF(vector<vector<int>> &state, int turn){
    vector<moveType> moves = successors(state, turn);
    vector<vector<int>> state2;
    pair<int , int> maxim = {INT_MIN, -1};
    for(int i = 0; i < moves.size(); i++){
        state2 = state;
        moveIt(state2, moves[i]);
        int val = evaluate(state2);
        if(val > maxim.first){
            maxim.first = val;
            maxim.second = i;
        }
    }
    int ind = maxim.second;
    cout << moves[ind].first.first << " " << moves[ind].first.second << " " << 
            moves[ind].second.first << " " << moves[ind].second.second << endl;
}

struct Node{
    moveType move;
    int val;
};

Node maxValue(vector<vector<int>> &state, int alpha, int beta, int turn, int level);
Node minValue(vector<vector<int>> &state, int alpha, int beta, int turn, int level);

void alphaBetaSearch(vector<vector<int>> &state, int turn){
    Node v = maxValue(state, INT_MIN, INT_MAX, -1, 0);
    cout << v.move.first.first << " " << v.move.first.second << " " <<
        v.move.second.first << " " << v.move.second.second << endl; 
}

Node maxValue(vector<vector<int>> &state, int alpha, int beta, int turn, int level){
    int term = utility(state);
    if(term == -1) return {{{0, 0}, {0,0}}, Terminal};
    else if(term == 1 || term == 2) return {{{0, 0}, {0,0}}, -Terminal};
    Node v;
    v.val = INT_MIN;
    vector<moveType> moves = successors(state, turn);
    vector<vector<int>> state2;
    pair<int , int> maxim = {INT_MIN, -1};
    for(int i = 0; i < moves.size(); i++){
        state2 = state;
        moveIt(state2, moves[i]);
        int val;
        if(level == maxLevel){
            val = evaluate(state2);
        }else{
            val = minValue(state2, alpha, beta, -turn, level+1).val;
        }
        if(val > v.val){
            v.val = val;
            v.move = moves[i];
        }
        if(v.val >= beta){
            return v;
        }
        alpha = max(alpha, v.val);
    }
    return v;
}

Node minValue(vector<vector<int>> &state, int alpha, int beta, int turn, int level){
    int term = utility(state);
    if(term == -1 || term == 2) return {{{0, 0}, {0,0}}, Terminal};
    else if(term == 1) return {{{0, 0}, {0,0}}, -Terminal};
    
    Node v;
    v.val = INT_MAX;
    vector<moveType> moves = successors(state, turn);
    vector<vector<int>> state2;
    pair<int , int> maxim = {INT_MIN, -1};
    for(int i = 0; i < moves.size(); i++){
        state2 = state;
        moveIt(state2, moves[i]);
        int val;
        if(level == maxLevel){
            val = evaluate(state2);
        }else{
            val = maxValue(state2, alpha, beta, -turn, level+1).val;
        }
        if(val < v.val){
            v.val = val;
            v.move = moves[i];
        }
        if(v.val <= alpha){
            return v;
        }
        beta = min(beta, v.val);
    }
    return v;
}


int main(){
    int turn;
    cin >> n;
    cin >> turn;
    vector< vector<int>> state = vector< vector<int> >(n, vector<int>(n, 0));
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            cin >> state[i][j];
        }
    }
    alphaBetaSearch(state, turn);
    return 0;
}