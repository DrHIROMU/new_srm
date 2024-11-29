import { useState } from "react";
import {
  useReactTable,
  getCoreRowModel,
  createColumnHelper,
} from "@tanstack/react-table";

import Vendor from "../../types/vendor/Vendor";

const defaultData: Vendor[] = [
  {
    id: 1,
    name: "Vendor 1",
    vendorCode: "111",
  },
  {
    id: 2,
    name: "Vendor 2",
    vendorCode: "222",
  },
  {
    id: 3,
    name: "Vendor 3",
    vendorCode: "333",
  },
];

const columnHelper = createColumnHelper<Vendor>();

const columns = [
  columnHelper.accessor("id", {
    header: () => "ID",
    cell: (info) => info.getValue(),
    footer: (info) => info.column.id,
  }),
  columnHelper.accessor("name", {
    header: () => "ID",
    cell: (info) => info.getValue(),
    footer: (info) => info.column.id,
  }),
  columnHelper.accessor("id", {
    header: () => "ID",
    cell: (info) => info.getValue(),
    footer: (info) => info.column.id,
  }),
];

const VendorQuery = () => {
  const [data, setData] = useState(() => [...defaultData]);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  return (
    <div>
      <table>
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th key={header.id}>
                  {header.isPlaceholder
                    ? null
                    : flexRender(
                        header.column.columnDef.header,
                        header.getContext()
                      )}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row) => (
            <tr key={row.id}>
              {row.getVisibleCells().map((cell) => (
                <td key={cell.id}>
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
        <tfoot>
          {table.getFooterGroups().map((footerGroup) => (
            <tr key={footerGroup.id}>
              {footerGroup.headers.map((header) => (
                <th key={header.id}>
                  {header.isPlaceholder
                    ? null
                    : flexRender(
                        header.column.columnDef.footer,
                        header.getContext()
                      )}
                </th>
              ))}
            </tr>
          ))}
        </tfoot>
      </table>
    </div>
  );
};

export default VendorQuery;
