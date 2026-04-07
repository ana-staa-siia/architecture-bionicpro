import React, { useState } from 'react';
import { useKeycloak } from '@react-keycloak/web';

interface Report {
  reportDate: string;
  userId: string;
  prosthesisId: string;
  userName: string;
  prosthesisModel: string;
  totalSessions: number;
  avgSignalStrength: number;
  totalMovements: number;
  errorCount: number;
}

const ReportPage: React.FC = () => {
  const { keycloak, initialized } = useKeycloak();
  const [loading, setLoading] = useState(false);
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [reports, setReports] = useState<Report[]>([]);

  const fetchReport = async () => {
    if (!keycloak?.token) {
      setError('Not authenticated');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const userId = keycloak.tokenParsed?.preferred_username || keycloak.tokenParsed?.sub;

      const response = await fetch(`${process.env.REACT_APP_API_URL}/reports?userId=${userId}`,
          {
            headers: {
              'Authorization': `Bearer ${keycloak.token}`
            }
          });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setReports(data);

    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const downloadReport = async () => {
    if (!keycloak?.token) {
      setError('Not authenticated');
      return;
    }

    try {
      setDownloading(true);
      setError(null);

      const userId = keycloak.tokenParsed?.preferred_username || keycloak.tokenParsed?.sub;

      const response = await fetch(`${process.env.REACT_APP_API_URL}/reports?userId=${userId}`,
          {
            headers: {
              'Authorization': `Bearer ${keycloak.token}`
            }
          });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `report_${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setDownloading(false);
    }
  };

  if (!initialized) {
    return <div>Loading...</div>;
  }

  if (!keycloak.authenticated) {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
          <button
              onClick={() => keycloak.login()}
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Login
          </button>
        </div>
    );
  }

  return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
        <div className="p-8 bg-white rounded-lg shadow-md w-full max-w-4xl">
          <h1 className="text-2xl font-bold mb-6">Usage Reports</h1>

          <div className="flex gap-4 mb-6">
            <button
                onClick={fetchReport}
                disabled={loading}
                className={`px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 ${
                    loading ? 'opacity-50 cursor-not-allowed' : ''
                }`}
            >
              {loading ? 'Loading...' : 'Show Report'}
            </button>

            <button
                onClick={downloadReport}
                disabled={downloading || reports.length === 0}
                className={`px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 ${
                    downloading || reports.length === 0 ? 'opacity-50 cursor-not-allowed' : ''
                }`}
            >
              {downloading ? 'Preparing...' : 'Download JSON'}
            </button>
          </div>

          {error && (
              <div className="mt-4 p-4 bg-red-100 text-red-700 rounded">
                {error}
              </div>
          )}

          {reports.length > 0 && (
              <div className="mt-6">
                <h2 className="text-xl font-bold mb-4">Your Reports</h2>
                <div className="overflow-x-auto">
                  <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                    <tr className="bg-gray-100">
                      <th className="px-4 py-2 border">Date</th>
                      <th className="px-4 py-2 border">Prosthesis</th>
                      <th className="px-4 py-2 border">Sessions</th>
                      <th className="px-4 py-2 border">Avg Signal</th>
                      <th className="px-4 py-2 border">Movements</th>
                      <th className="px-4 py-2 border">Errors</th>
                    </tr>
                    </thead>
                    <tbody>
                    {reports.map((report, index) => (
                        <tr key={index} className="hover:bg-gray-50">
                          <td className="px-4 py-2 border text-center">{report.reportDate}</td>
                          <td className="px-4 py-2 border">{report.prosthesisModel}</td>
                          <td className="px-4 py-2 border text-center">{report.totalSessions}</td>
                          <td className="px-4 py-2 border text-center">{report.avgSignalStrength}</td>
                          <td className="px-4 py-2 border text-center">{report.totalMovements}</td>
                          <td className="px-4 py-2 border text-center">{report.errorCount}</td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
              </div>
          )}
        </div>
      </div>
  );
};

export default ReportPage;