import React from 'react';
import { Award, Zap, BarChart3, Code } from 'lucide-react';
import FitnessChart from './FitnessChart';
import { ClassificationResult } from '@/app/page';

interface ResultsDashboardProps {
  result: ClassificationResult;
}

export default function ResultsDashboard({ result }: ResultsDashboardProps) {
  const metrics = [
    { label: 'Training Accuracy', value: ((result?.trainAccuracy || 0) * 100).toFixed(2) + '%', icon: Award, color: 'text-blue-600', bg: 'bg-blue-50' },
    { label: 'Test Accuracy', value: ((result?.testAccuracy || 0) * 100).toFixed(2) + '%', icon: Zap, color: 'text-green-600', bg: 'bg-green-50' },
    { label: 'F-Measure', value: ((result?.fMeasure || 0) * 100).toFixed(2) + '%', icon: BarChart3, color: 'text-purple-600', bg: 'bg-purple-50' },
  ];

  return (
    <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {metrics.map((metric) => (
          <div key={metric.label} className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className={`p-3 rounded-lg ${metric.bg}`}>
              <metric.icon className={`w-6 h-6 ${metric.color}`} />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-600 uppercase tracking-wider">{metric.label}</p>
              <p className="text-2xl font-bold text-gray-900">{metric.value}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm">
        <h3 className="text-lg font-semibold mb-6 flex items-center gap-2 text-gray-900">
          <BarChart3 className="w-5 h-5 text-blue-500" />
          Fitness History
        </h3>
        <FitnessChart data={result?.history || []} />
      </div>

      <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2 text-gray-900">
          <Code className="w-5 h-5 text-blue-500" />
          Best Individual Model
        </h3>
        <div className="bg-gray-900 rounded-lg p-6 font-mono text-sm text-blue-400 overflow-x-auto whitespace-pre-wrap leading-relaxed shadow-inner border-2 border-gray-800">
          {result?.modelDescription || 'No model description available'}
        </div>
        <div className="mt-4 flex justify-between items-center text-xs text-gray-600">
          <span>Runtime: {result?.runtimeMs || 0}ms</span>
          <span>Evolutionary Strategy: Genetic Programming</span>
        </div>
      </div>
    </div>
  );
}
