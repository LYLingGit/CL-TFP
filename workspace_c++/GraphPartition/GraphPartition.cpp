#include <vector>
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <Windows.h>
#include "D:\Program Files (x86)\metis-5.1.0\metis-5.1.0\include\metis.h"
#include <time.h>
using namespace std;

vector<idx_t> func(vector<idx_t> xadj, vector<idx_t> adjncy, vector<idx_t> vwgt, idx_t nParts) {
	idx_t nVertices = xadj.size() - 1; // 节点数
	idx_t nEdges = adjncy.size() / 2;    // 边数
	idx_t nWeights = 1;
	idx_t objval;
	std::vector<idx_t> part(nVertices, 0);

	idx_t options[METIS_NOPTIONS];
	METIS_SetDefaultOptions(options);
	options[METIS_OPTION_CONTIG] = 1;
	int ret = METIS_PartGraphKway(&nVertices, &nWeights, xadj.data(), adjncy.data(),
		NULL, NULL, vwgt.data(), &nParts, NULL,
		NULL, options, &objval, part.data());
	return part;
}


int main() {
	int caseNum = 3;
	LARGE_INTEGER cpuFreq, startTime, endTime;
	for (int i = 2; i < 3; i++) {
		cout << "==========case" + to_string(i + 1) + "===========" << endl;
		QueryPerformanceCounter(&startTime);
		QueryPerformanceFrequency(&cpuFreq);
		ifstream ingraph("D:/Code/TeamFormation/workspace_c++/Data/Experiment1/case" + to_string(i + 1) + "/graph.txt");
		if (!ingraph) {
			cout << "打开文件graph失败！" << endl;
			exit(1);//失败退回操作系统    
		}
		int vexnum, edgenum, nParts;
		string line;
		getline(ingraph, line);
		istringstream tmp(line);
		tmp >> vexnum >> edgenum >> nParts;
		vector<idx_t> xadj(0);
		vector<idx_t> adjncy(0); //点的id从0开始
		vector<idx_t> vwgt(0);

		idx_t a, w;
		for (int i = 0; i < vexnum; i++) {
			xadj.push_back(adjncy.size());
			getline(ingraph, line);
			istringstream tmp(line);
			while (tmp >> a >> w) {
				adjncy.push_back(a);
				vwgt.push_back(w);
			}
		}
		xadj.push_back(adjncy.size());
		ingraph.close();

		vector<idx_t> part = func(xadj, adjncy, vwgt, nParts);
		ofstream outpartition("D:/Code/TeamFormation/workspace_c++/Data/Experiment1/case" + to_string(i + 1) + "/partition.txt");
		if (!outpartition) {
			cout << "打开文件失败！" << endl;
			exit(1);
		}
		for (int i = 0; i < part.size(); i++) {
			outpartition << i << " " << part[i] << endl;
		}
		outpartition.close();
		QueryPerformanceCounter(&endTime);
		double runTime = (((endTime.QuadPart - startTime.QuadPart) * 1000.0f) / cpuFreq.QuadPart);
		ofstream outtime("D:/Code/TeamFormation/workspace_c++/Data/Experiment1/case" + to_string(i + 1) + "/time.txt");
		if (!outtime) {
			cout << "打开文件失败！" << endl;
			exit(1);
		}
		outtime << runTime;
	}
}
